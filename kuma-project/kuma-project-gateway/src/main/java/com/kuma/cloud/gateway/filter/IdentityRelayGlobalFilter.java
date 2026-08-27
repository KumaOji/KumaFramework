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
import com.kuma.cloud.gateway.properties.GatewayCloudProperties;
import com.kuma.cloud.gateway.support.ClientIpResolver;
import com.kuma.cloud.gateway.support.GatewayHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 在 JWT 校验通过后，将身份声明透传给下游服务，并标记请求来自网关。
 *
 * @author kuma
 * @since 2026-04-23
 */
public class IdentityRelayGlobalFilter implements GlobalFilter, Ordered {

    private final GatewayCloudProperties properties;

    public IdentityRelayGlobalFilter(GatewayCloudProperties properties) {
        this.properties = properties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
        String clientIp = ClientIpResolver.resolve(exchange.getRequest(), properties.isTrustProxyHeaders());
        builder.header(GatewayHeaders.GATEWAY_SOURCE, GatewayHeaders.GATEWAY_SOURCE_VALUE);
        builder.header(GatewayHeaders.CLIENT_IP, clientIp);
        builder.header(CommonConstants.KMC_FROM_INNER, "true");

        return exchange.getPrincipal()
                .filter(principal -> principal instanceof Authentication)
                .cast(Authentication.class)
                .flatMap(authentication -> enrichIdentity(builder, authentication))
                .defaultIfEmpty(builder)
                .flatMap(requestBuilder -> chain.filter(
                        exchange.mutate().request(requestBuilder.build()).build()));
    }

    private Mono<ServerHttpRequest.Builder> enrichIdentity(
            ServerHttpRequest.Builder builder, Authentication authentication) {
        if (!properties.getAuth().isRelayIdentity()) {
            return Mono.just(builder);
        }
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            return Mono.just(builder);
        }
        Jwt jwt = jwtAuthentication.getToken();
        GatewayCloudProperties.Auth auth = properties.getAuth();

        String username = jwt.getClaimAsString(auth.getUsernameClaim());
        if (!StringUtils.hasText(username)) {
            username = jwt.getSubject();
        }
        if (StringUtils.hasText(username)) {
            builder.header(GatewayHeaders.USER_NAME, username);
        }

        Object uid = jwt.getClaim("uid");
        if (uid != null) {
            builder.header(GatewayHeaders.USER_ID, String.valueOf(uid));
        } else if (StringUtils.hasText(jwt.getSubject())) {
            builder.header(GatewayHeaders.USER_ID, jwt.getSubject());
        }

        List<String> authorities = jwt.getClaimAsStringList(auth.getAuthoritiesClaim());
        if (authorities == null || authorities.isEmpty()) {
            authorities = authentication.getAuthorities().stream()
                    .map(granted -> granted.getAuthority())
                    .collect(Collectors.toList());
        }
        if (!authorities.isEmpty()) {
            builder.header(GatewayHeaders.USER_AUTHORITIES, String.join(",", authorities));
        }

        String clientId = jwt.getClaimAsString("client_id");
        if (StringUtils.hasText(clientId)) {
            builder.header(GatewayHeaders.CLIENT_ID, clientId);
        }
        return Mono.just(builder);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 200;
    }
}
