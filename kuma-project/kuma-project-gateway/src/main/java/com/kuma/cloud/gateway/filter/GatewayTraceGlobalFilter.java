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
import java.util.HashMap;
import java.util.Map;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
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
            String correlationId = suppliedId != null && suppliedId.matches("[A-Za-z0-9_-]{1,128}")
                    ? suppliedId : span != null ? span.context().traceId() : UUID.randomUUID().toString().replace("-", "");
            String traceId = span != null ? span.context().traceId() : correlationId;
            String spanId = span != null ? span.context().spanId() : "";
            String tenant = exchange.getRequest().getQueryParams().getFirst(CommonConstants.KMC_TENANT_ID);
            if (tenant == null || tenant.isBlank()) {
                tenant = exchange.getRequest().getHeaders().getFirst(CommonConstants.KMC_TENANT_ID);
            }
            String tenantId = bounded(tenant, CommonConstants.KMC_TENANT_ID_DEFAULT);
            String version = bounded(exchange.getRequest().getHeaders().getFirst(CommonConstants.KMC_REQUEST_VERSION), "");
            Map<String, String> mdc = new HashMap<>();
            mdc.put(CommonConstants.KMC_TRACE_ID, correlationId);
            mdc.put(CommonConstants.KMC_TENANT_ID, tenantId);
            if (!version.isEmpty()) mdc.put(CommonConstants.KMC_REQUEST_VERSION, version);
            if (span != null) {
                mdc.put("traceId", traceId);
                mdc.put("spanId", spanId);
                exchange.getResponse().getHeaders().set(CommonConstants.OTLP_TRACE_ID, traceId);
                exchange.getResponse().getHeaders().set(CommonConstants.OTLP_SPANE_ID, spanId);
            }
            String skywalkingId = TraceContext.traceId();
            if (skywalkingId == null || skywalkingId.equals("N/A") || skywalkingId.equals("Ignored_Trace")) {
                skywalkingId = exchange.getAttribute(GatewayHeaders.SKYWALKING_ID_ATTRIBUTE);
            }
            if (skywalkingId != null && !skywalkingId.equals("N/A") && !skywalkingId.equals("Ignored_Trace")) {
                mdc.put("tid", skywalkingId);
                exchange.getResponse().getHeaders().set("tid", skywalkingId);
            }
            exchange.getAttributes().put(GatewayHeaders.MDC_ATTRIBUTE, Map.copyOf(mdc));
            exchange.getAttributes().put(GatewayHeaders.TRACE_ID_ATTRIBUTE, traceId);
            exchange.getAttributes().put(GatewayHeaders.SPAN_ID_ATTRIBUTE, spanId);
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .headers(headers -> {
                        headers.set(CommonConstants.KMC_TRACE_ID, correlationId);
                        headers.set(CommonConstants.KMC_TENANT_ID, tenantId);
                        if (!version.isEmpty()) headers.set(CommonConstants.KMC_REQUEST_VERSION, version);
                        else headers.remove(CommonConstants.KMC_REQUEST_VERSION);
                    }).build();
            exchange.getResponse().getHeaders().set(CommonConstants.KMC_TRACE_ID, correlationId);
            exchange.getResponse().getHeaders().set(CommonConstants.KMC_TENANT_ID, tenantId);
            if (!version.isEmpty()) exchange.getResponse().getHeaders().set(CommonConstants.KMC_REQUEST_VERSION, version);
            return chain.filter(exchange.mutate().request(request).build())
                    .contextWrite(context -> context.put(CommonConstants.KMC_TRACE_ID, correlationId)
                            .put(GatewayHeaders.MDC_ATTRIBUTE, Map.copyOf(mdc)));
        });
    }

    private static String bounded(String value, String fallback) {
        return value != null && value.matches("[A-Za-z0-9_.-]{1,128}") ? value : fallback;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 20;
    }
}
