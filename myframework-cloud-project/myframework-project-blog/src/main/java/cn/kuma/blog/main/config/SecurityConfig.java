package cn.kuma.blog.main.config;

import cn.kuma.blog.framework.util.JwtUtil;
import cn.kuma.blog.main.security.JsonAccessDeniedHandler;
import cn.kuma.blog.main.security.JsonAuthenticationEntryPoint;
import cn.kuma.blog.main.security.TokenAuthenticationFilter;
import cn.kuma.blog.main.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置：基于 Cookie Token 的认证 + 方法级授权（@PreAuthorize）。
 *
 * @author Kuma
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    public SecurityConfig(TokenService tokenService, JwtUtil jwtUtil) {
        // 注入 JwtUtil 以在启动时触发其 @Value(secret/expiration) 注入，避免登录时静态方法调用 secret 未设置
        this.tokenAuthenticationFilter = new TokenAuthenticationFilter(tokenService);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/**")
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                    .requestMatchers("/auth/login", "/auth/logout", "/auth/current").permitAll()
                    .requestMatchers("/error").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger**", "/doc**", "/docs**", "/api-doc**").permitAll()
                    .requestMatchers("/article/list", "/article/category/list", "/article/category/counts", "/article/category/*").permitAll()
                    .requestMatchers("/article/*/view", "/article/*/like", "/article/*/comment").permitAll()
                    .requestMatchers("/music/list", "/music/recommend", "/music/hot", "/music/search").permitAll()
                    .requestMatchers("/music/*/stream", "/music/*/play").permitAll()
                    .requestMatchers("/article/*").permitAll()
                    .requestMatchers("/music/*").permitAll()
                    .requestMatchers("/ready/list").permitAll()
                    .requestMatchers("/python/**").hasRole("ADMIN")
                    .anyRequest().authenticated())
            .exceptionHandling(e -> e
                    .authenticationEntryPoint(new JsonAuthenticationEntryPoint())
                    .accessDeniedHandler(new JsonAccessDeniedHandler()))
            .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
