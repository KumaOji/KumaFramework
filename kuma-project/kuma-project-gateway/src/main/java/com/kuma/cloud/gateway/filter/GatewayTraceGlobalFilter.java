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
import com.kuma.cloud.gateway.support.GatewayHeaders;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import java.util.UUID;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 关联 ID 传播：优先采用当前 OTel span 的 traceId，并透传 {@code kmc-trace-id}。
 *
 * <p>使用 WebFilter 覆盖未匹配路由和鉴权失败；ID 保存于 exchange/Reactor context，避免在线程池中泄漏 ThreadLocal。
 *
 * @author kuma
 * @since 2026-04-23
 */
public class GatewayTraceGlobalFilter implements WebFilter, Ordered {

    private final ObjectProvider<Tracer> tracers;

    public GatewayTraceGlobalFilter(ObjectProvider<Tracer> tracers) {
        this.tracers = tracers;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.defer(() -> {
            Tracer tracer = tracers.getIfAvailable();
            Span span = tracer != null ? tracer.currentSpan() : null;
            String suppliedId = exchange.getRequest().getHeaders().getFirst(CommonConstants.KMC_TRACE_ID);
            String traceId = span != null ? span.context().traceId()
                    : suppliedId != null && suppliedId.matches("[A-Za-z0-9_-]{1,128}")
                    ? suppliedId : UUID.randomUUID().toString().replace("-", "");
            String spanId = span != null ? span.context().spanId() : "";
            exchange.getAttributes().put(GatewayHeaders.TRACE_ID_ATTRIBUTE, traceId);
            exchange.getAttributes().put(GatewayHeaders.SPAN_ID_ATTRIBUTE, spanId);
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .headers(headers -> headers.set(CommonConstants.KMC_TRACE_ID, traceId)).build();
            exchange.getResponse().getHeaders().set(CommonConstants.KMC_TRACE_ID, traceId);
            return chain.filter(exchange.mutate().request(request).build())
                    .contextWrite(context -> context.put(CommonConstants.KMC_TRACE_ID, traceId));
        });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 20;
    }
}
