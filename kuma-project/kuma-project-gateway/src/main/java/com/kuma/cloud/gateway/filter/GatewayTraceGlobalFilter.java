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

package com.kuma.cloud.gateway.filter;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.holder.TraceContextHolder;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import cn.hutool.core.util.StrUtil;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全链路 TraceId 传播：读取或生成 {@code kmc-trace-id}，写入 MDC / {@link TraceContextHolder} 并透传给下游。
 *
 * <p>与 {@code kuma-boot-starter-web} 的 {@code TraceFilter} 保持同一套 ID 生成与 MDC 键名约定。
 *
 * @author kuma
 * @since 2026-04-23
 */
public class GatewayTraceGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String traceId = request.getHeaders().getFirst(CommonConstants.KMC_TRACE_ID);
        if (StrUtil.isBlank(traceId)) {
            traceId = IdGeneratorUtils.getIdStr();
        }
        String finalTraceId = traceId;

        ServerHttpRequest mutatedRequest = request.mutate()
                .header(CommonConstants.KMC_TRACE_ID, finalTraceId)
                .build();
        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
        mutatedExchange.getResponse().getHeaders().set(CommonConstants.KMC_TRACE_ID, finalTraceId);

        return chain.filter(mutatedExchange)
                .doOnSubscribe(subscription -> bindTraceContext(finalTraceId))
                .doFinally(signal -> clearTraceContext());
    }

    private static void bindTraceContext(String traceId) {
        TraceContextHolder.setTraceId(traceId);
        TraceUtils.setKmcTraceId(traceId);
    }

    private static void clearTraceContext() {
        TraceContextHolder.clear();
        TraceUtils.removeKmcTraceId();
        MDC.remove(CommonConstants.KMC_TRACE_ID);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 20;
    }
}
