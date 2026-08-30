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

package com.kuma.cloud.gateway.config;

import com.kuma.cloud.gateway.properties.GatewayCloudProperties;
import com.kuma.cloud.gateway.support.ClientIpResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

/**
 * 限流键解析器，供路由上的 {@code RequestRateLimiter} 过滤器引用。
 *
 * @author kuma
 * @since 2026-04-23
 */
@Configuration(proxyBeanMethods = false)
public class RateLimitConfiguration {

    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "userOrIpKeyResolver")
    public KeyResolver userOrIpKeyResolver(GatewayCloudProperties properties) {
        return exchange -> exchange.getPrincipal()
                .filter(JwtAuthenticationToken.class::isInstance)
                .cast(Authentication.class)
                .map(Authentication::getName)
                .switchIfEmpty(Mono.defer(() -> {
                    if (!properties.getRateLimit().isFallbackToIp()) {
                        return Mono.just("anonymous");
                    }
                    String ip = ClientIpResolver.resolve(
                            exchange.getRequest(), properties.isTrustProxyHeaders());
                    return Mono.just("ip:" + ip);
                }))
                .map(key -> {
                    if (!properties.getRateLimit().isIncludePath()) {
                        return key;
                    }
                    return key + ":" + exchange.getRequest().getURI().getPath();
                });
    }

    @Bean
    @ConditionalOnMissingBean(name = "ipKeyResolver")
    public KeyResolver ipKeyResolver(GatewayCloudProperties properties) {
        return exchange -> Mono.just(ClientIpResolver.resolve(
                exchange.getRequest(), properties.isTrustProxyHeaders()));
    }
}
