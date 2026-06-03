/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.webflux.filter;

import com.kuma.boot.common.constant.CommonConstants;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 响应式链路追踪 WebFilter
 *
 * <p>从请求头读取 traceId（{@value CommonConstants#TRACE_ID_HEADER}），若不存在则生成新值，
 * 写入 MDC 并通过 Reactor Context 传播到后续操作符。
 *
 * @author kuma
 */
public class TraceMdcWebFilter implements WebFilter, Ordered {

    private static final String MDC_TRACE_KEY = "traceId";

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();

        String traceId = headers.getFirst(CommonConstants.KMC_TRACE_ID);
        if (!StringUtils.hasText(traceId)) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }

        final String finalTraceId = traceId;

        // 将 traceId 写回响应头，方便前端或网关读取
        ServerWebExchange mutatedExchange = exchange.mutate()
                .response(exchange.getResponse())
                .build();
        mutatedExchange.getResponse().getHeaders().set(CommonConstants.KMC_TRACE_ID, finalTraceId);

        return chain.filter(mutatedExchange)
                .contextWrite(ctx -> ctx.put(MDC_TRACE_KEY, finalTraceId))
                .doOnSubscribe(s -> MDC.put(MDC_TRACE_KEY, finalTraceId))
                .doFinally(signal -> MDC.remove(MDC_TRACE_KEY));
    }
}
