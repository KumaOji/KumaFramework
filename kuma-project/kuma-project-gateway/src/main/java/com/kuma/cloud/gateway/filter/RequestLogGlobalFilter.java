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

import com.kuma.boot.ip2region.model.Ip2regionSearcher;
import com.kuma.cloud.gateway.properties.GatewayCloudProperties;
import com.kuma.cloud.gateway.support.ClientIpResolver;
import com.kuma.cloud.gateway.support.GatewayHeaders;
import com.kuma.cloud.gateway.support.GatewayPathMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.spi.LoggingEventBuilder;
import java.util.concurrent.atomic.AtomicReference;
import reactor.core.publisher.Mono;


import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * 访问日志：记录路由、客户端 IP、归属地、状态码与耗时，慢请求以 WARN 输出。
 *
 * <p>IP 归属地解析复用 {@code kuma-boot-starter-ip2region} 的 {@link Ip2regionSearcher}。
 *
 * @author kuma
 * @since 2026-04-23
 */
public class RequestLogGlobalFilter implements WebFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLogGlobalFilter.class);

    private final GatewayCloudProperties properties;
    private final ObjectProvider<Ip2regionSearcher> ip2regionSearcher;

    public RequestLogGlobalFilter(
            GatewayCloudProperties properties, ObjectProvider<Ip2regionSearcher> ip2regionSearcher) {
        this.properties = properties;
        this.ip2regionSearcher = ip2regionSearcher;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        GatewayCloudProperties.Log logConfig = properties.getLog();
        String path = exchange.getRequest().getURI().getPath();
        if (GatewayPathMatcher.matchesAny(path, logConfig.getExcludePaths())) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        String method = request.getMethod().name();
        String clientIp = ClientIpResolver.resolve(request, properties.isTrustProxyHeaders());
        String geo = resolveGeo(clientIp);
        long start = System.nanoTime();
        AtomicReference<Throwable> failure = new AtomicReference<>();
        if (logConfig.isResponseTimeHeader()) {
            exchange.getResponse().beforeCommit(() -> {
                exchange.getResponse().getHeaders().set(GatewayHeaders.RESPONSE_TIME,
                        ((System.nanoTime() - start) / 1_000_000) + "ms");
                return Mono.empty();
            });
        }

        return chain.filter(exchange).doOnError(failure::set).doFinally(signal -> {
            long cost = (System.nanoTime() - start) / 1_000_000;
            Throwable error = failure.get();
            int status = exchange.getResponse().getStatusCode() != null
                    ? exchange.getResponse().getStatusCode().value()
                    : error instanceof ResponseStatusException responseError ? responseError.getStatusCode().value()
                    : error != null ? 500 : signal == reactor.core.publisher.SignalType.CANCEL ? 499 : 200;
            Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
            String routeId = route != null ? route.getId() : "-";

            LoggingEventBuilder event = status >= 500 ? log.atError()
                    : cost >= logConfig.getSlowThreshold().toMillis() ? log.atWarn() : log.atInfo();
            event.addKeyValue("event", "gateway_access")
                    .addKeyValue("method", method).addKeyValue("path", path)
                    .addKeyValue("status", status).addKeyValue("duration_ms", cost)
                    .addKeyValue("route_id", routeId).addKeyValue("client_ip", clientIp)
                    .addKeyValue("traceId", exchange.getAttributeOrDefault(GatewayHeaders.TRACE_ID_ATTRIBUTE, ""))
                    .addKeyValue("spanId", exchange.getAttributeOrDefault(GatewayHeaders.SPAN_ID_ATTRIBUTE, ""));
            if (geo != null) event.addKeyValue("geo", geo);
            event.log("Gateway request completed");
        });
    }

    private String resolveGeo(String clientIp) {
        Ip2regionSearcher searcher = ip2regionSearcher.getIfAvailable();
        if (searcher == null || !StringUtils.hasText(clientIp) || "unknown".equals(clientIp)) {
            return null;
        }
        return searcher.getAddressAndIsp(clientIp);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 30;
    }
}
