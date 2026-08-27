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

import com.kuma.cloud.gateway.properties.GatewayCloudProperties;
import com.kuma.cloud.gateway.support.ClientIpResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * IP 黑白名单访问控制。
 *
 * @author kuma
 * @since 2026-04-23
 */
public class ClientAccessGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(ClientAccessGlobalFilter.class);

    private final GatewayCloudProperties properties;
    private final List<IpAddressMatcher> whiteListMatchers = new ArrayList<>();
    private final List<IpAddressMatcher> blackListMatchers = new ArrayList<>();

    public ClientAccessGlobalFilter(GatewayCloudProperties properties) {
        this.properties = properties;
        reloadMatchers();
    }

    private void reloadMatchers() {
        whiteListMatchers.clear();
        blackListMatchers.clear();
        properties.getAccess().getWhiteList().forEach(pattern -> whiteListMatchers.add(new IpAddressMatcher(pattern)));
        properties.getAccess().getBlackList().forEach(pattern -> blackListMatchers.add(new IpAddressMatcher(pattern)));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!properties.getAccess().isEnabled()) {
            return chain.filter(exchange);
        }
        reloadMatchers();

        String clientIp = ClientIpResolver.resolve(exchange.getRequest(), properties.isTrustProxyHeaders());
        if (!whiteListMatchers.isEmpty() && whiteListMatchers.stream().noneMatch(matcher -> matcher.matches(clientIp))) {
            log.warn("[Gateway] IP not in white list: {} {}", clientIp, exchange.getRequest().getURI().getPath());
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
        if (blackListMatchers.stream().anyMatch(matcher -> matcher.matches(clientIp))) {
            log.warn("[Gateway] IP blocked by black list: {} {}", clientIp, exchange.getRequest().getURI().getPath());
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
