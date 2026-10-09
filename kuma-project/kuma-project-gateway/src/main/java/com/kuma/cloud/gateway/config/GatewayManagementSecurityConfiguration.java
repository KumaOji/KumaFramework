package com.kuma.cloud.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;

/** 管理端口提供只读健康/指标接口，业务端口拒绝访问这些管理接口。 */
@Configuration(proxyBeanMethods = false)
@Profile("observability")
public class GatewayManagementSecurityConfiguration {
    @Bean
    @Order(-1)
    public SecurityWebFilterChain gatewayManagementSecurityWebFilterChain(
            ServerHttpSecurity http, @Value("${management.server.port}") int managementPort) {
        return http.securityMatcher(exchange -> {
                    var address = exchange.getRequest().getLocalAddress();
                    return address != null && address.getPort() == managementPort
                            ? ServerWebExchangeMatcher.MatchResult.match()
                            : ServerWebExchangeMatcher.MatchResult.notMatch();
                })
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/health/**", "/actuator/info", "/actuator/prometheus").permitAll()
                        .anyExchange().denyAll())
                .build();
    }
}
