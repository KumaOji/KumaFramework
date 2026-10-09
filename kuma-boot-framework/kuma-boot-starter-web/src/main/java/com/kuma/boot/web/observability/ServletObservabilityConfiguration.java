package com.kuma.boot.web.observability;

import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@Profile("observability")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletObservabilityConfiguration {
    @Bean
    public FilterRegistrationBean<ServletAccessLogFilter> observabilityAccessLogFilter(ObjectProvider<Tracer> tracers) {
        var bean = new FilterRegistrationBean<>(new ServletAccessLogFilter(tracers));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        bean.setAsyncSupported(true);
        return bean;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain observabilityManagementSecurityFilterChain(HttpSecurity http,
            @Value("${management.server.port}") int managementPort,
            @Value("${server.port:8080}") int applicationPort) throws Exception {
        if (managementPort == applicationPort) {
            throw new IllegalStateException("Observability requires a separate management port");
        }
        return http.securityMatcher(request -> request.getLocalPort() == managementPort
                        || request.getRequestURI().equals(request.getContextPath() + "/actuator/prometheus"))
                .csrf(csrf -> csrf.disable())
                .requestCache(cache -> cache.disable())
                // Set the status directly: sendError would dispatch to a business error
                // controller, whose response wrapper can replace a rejection with HTTP 200.
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, error) -> response.setStatus(401))
                        .accessDeniedHandler((request, response, error) -> response.setStatus(403)))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(request -> request.getLocalPort() == managementPort
                                && (request.getRequestURI().startsWith("/actuator/health")
                                    || request.getRequestURI().equals("/actuator/prometheus")
                                    || request.getRequestURI().equals("/actuator/info"))).permitAll()
                        .anyRequest().denyAll())
                .build();
    }
}
