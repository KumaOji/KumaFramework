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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * 网关安全：JWT 资源服务器统一鉴权，白名单路径放行。
 *
 * @author kuma
 * @since 2026-04-23
 */
@Configuration(proxyBeanMethods = false)
@EnableWebFluxSecurity
@ConditionalOnProperty(prefix = GatewayCloudProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class GatewaySecurityConfiguration {

    @Bean
    @Order(0)
    public SecurityWebFilterChain gatewaySecurityWebFilterChain(
            ServerHttpSecurity http, GatewayCloudProperties properties) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable);

        if (properties.getAuth().isEnabled()) {
            http.authorizeExchange(exchanges -> {
                exchanges.pathMatchers(properties.getAuth().getWhiteList().toArray(String[]::new))
                        .permitAll();
                exchanges.anyExchange().authenticated();
            }).oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        } else {
            http.authorizeExchange(exchanges -> exchanges.anyExchange().permitAll());
        }
        return http.build();
    }
}
