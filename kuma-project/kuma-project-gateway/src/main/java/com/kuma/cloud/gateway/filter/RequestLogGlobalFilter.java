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
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * 访问日志：记录路由、客户端 IP、归属地、状态码与耗时，慢请求以 WARN 输出。
 *
 * <p>IP 归属地解析复用 {@code kuma-boot-starter-ip2region} 的 {@link Ip2regionSearcher}。
 *
 * @author kuma
 * @since 2026-04-23
 */
public class RequestLogGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLogGlobalFilter.class);

    private final GatewayCloudProperties properties;
    private final ObjectProvider<Ip2regionSearcher> ip2regionSearcher;

    public RequestLogGlobalFilter(
            GatewayCloudProperties properties, ObjectProvider<Ip2regionSearcher> ip2regionSearcher) {
        this.properties = properties;
        this.ip2regionSearcher = ip2regionSearcher;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        GatewayCloudProperties.Log logConfig = properties.getLog();
        String path = exchange.getRequest().getURI().getPath();
        if (GatewayPathMatcher.matchesAny(path, logConfig.getExcludePaths())) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        String method = request.getMethod().name();
        String clientIp = ClientIpResolver.resolve(request, properties.isTrustProxyHeaders());
        String geo = resolveGeo(clientIp);
        long start = Instant.now().toEpochMilli();
        log.info("[Gateway] --> {} {} ip={}{}", method, path, clientIp, formatGeo(geo));

        return chain.filter(exchange).doFinally(signal -> {
            long cost = Instant.now().toEpochMilli() - start;
            int status = exchange.getResponse().getStatusCode() != null
                    ? exchange.getResponse().getStatusCode().value() : 0;
            Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
            String routeId = route != null ? route.getId() : "-";

            if (logConfig.isResponseTimeHeader()) {
                exchange.getResponse().getHeaders().set(GatewayHeaders.RESPONSE_TIME, cost + "ms");
            }

            String message = String.format(
                    "[Gateway] <-- %s %s %d %dms route=%s ip=%s%s",
                    method, path, status, cost, routeId, clientIp, formatGeo(geo));
            if (cost >= logConfig.getSlowThreshold().toMillis()) {
                log.warn("{} (slow)", message);
            } else {
                log.info(message);
            }
        });
    }

    private String resolveGeo(String clientIp) {
        Ip2regionSearcher searcher = ip2regionSearcher.getIfAvailable();
        if (searcher == null || !StringUtils.hasText(clientIp) || "unknown".equals(clientIp)) {
            return null;
        }
        return searcher.getAddressAndIsp(clientIp);
    }

    private static String formatGeo(String geo) {
        return StringUtils.hasText(geo) ? " geo=" + geo : "";
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 100;
    }
}
