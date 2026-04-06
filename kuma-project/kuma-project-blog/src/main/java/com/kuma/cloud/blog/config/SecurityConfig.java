package com.kuma.cloud.blog.config;

import com.kuma.cloud.blog.security.JsonAccessDeniedHandler;
import com.kuma.cloud.blog.security.JsonAuthenticationEntryPoint;
import com.kuma.cloud.blog.security.TokenAuthenticationFilter;
import com.kuma.cloud.blog.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    public SecurityConfig(TokenService tokenService) {
        this.tokenAuthenticationFilter = new TokenAuthenticationFilter(tokenService);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain blogSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/auth/login", "/auth/logout", "/auth/current").permitAll()
                        .requestMatchers("/auth/totp/**").authenticated()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/actuator/health").permitAll()  // 供负载均衡健康检查
                        .requestMatchers("/actuator/**").authenticated()  // 其余 actuator 需认证
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/doc.html", "/webjars/**").permitAll()
                        .requestMatchers("/article/list", "/article/category/**").permitAll()
                        .requestMatchers("/article/*/view", "/article/*/like", "/article/*/comment").permitAll()
                        .requestMatchers("/music/list", "/music/recommend", "/music/hot").permitAll()
                        .requestMatchers("/music/*/stream", "/music/*/play").permitAll()
                        .requestMatchers("/article/*").permitAll()
                        .requestMatchers("/music/*").permitAll()
                        .requestMatchers("/ready/list").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new JsonAuthenticationEntryPoint())
                        .accessDeniedHandler(new JsonAccessDeniedHandler()))
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
