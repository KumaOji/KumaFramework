package com.kuma.cloud.blog.config;

import com.kuma.cloud.blog.security.JsonAccessDeniedHandler;
import com.kuma.cloud.blog.security.JsonAuthenticationEntryPoint;
import com.kuma.cloud.blog.security.BlogJwtAuthenticationConverter;
import com.kuma.cloud.blog.security.CookieBearerTokenResolver;
import com.kuma.cloud.blog.security.OAuth2CookieService;
import com.kuma.cloud.blog.security.OAuth2LoginFailureHandler;
import com.kuma.cloud.blog.security.OAuth2LoginSuccessHandler;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private static final Set<String> SAFE_METHODS = Set.of("GET", "HEAD", "OPTIONS", "TRACE");

    private final JwtDecoder jwtDecoder;
    private final BlogJwtAuthenticationConverter jwtAuthenticationConverter;
    private final OAuth2LoginSuccessHandler loginSuccessHandler;
    private final OAuth2LoginFailureHandler loginFailureHandler;

    public SecurityConfig(
            JwtDecoder jwtDecoder,
            BlogJwtAuthenticationConverter jwtAuthenticationConverter,
            OAuth2LoginSuccessHandler loginSuccessHandler,
            OAuth2LoginFailureHandler loginFailureHandler) {
        this.jwtDecoder = jwtDecoder;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
        this.loginSuccessHandler = loginSuccessHandler;
        this.loginFailureHandler = loginFailureHandler;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain blogSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/**")
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .requireCsrfProtectionMatcher(SecurityConfig::requiresCookieCsrfProtection))
                // Authorization Code 的 state 在回调前临时使用 Session，成功后立即销毁。
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(a -> a
                        .dispatcherTypeMatchers(DispatcherType.ASYNC, DispatcherType.ERROR).permitAll()
                        .requestMatchers("/auth/login", "/auth/logout", "/auth/refresh", "/auth/csrf").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
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
                        .requestMatchers("/project/list", "/project/*/view").permitAll()
                        .requestMatchers("/project/*").permitAll()
                        .requestMatchers("/message/list", "/message", "/message/*/like").permitAll()
                        .requestMatchers("/friend-link/list", "/friend-link/apply", "/friend-link/*/view").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/chat/room/list", "/chat/room/*/history", "/chat/room/*/online").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new JsonAuthenticationEntryPoint())
                        .accessDeniedHandler(new JsonAccessDeniedHandler()))
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailureHandler))
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .bearerTokenResolver(new CookieBearerTokenResolver(
                                OAuth2CookieService.ACCESS_TOKEN_COOKIE))
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter))
                        .authenticationEntryPoint(new JsonAuthenticationEntryPoint())
                        .accessDeniedHandler(new JsonAccessDeniedHandler()));
        return http.build();
    }

    private static boolean requiresCookieCsrfProtection(HttpServletRequest request) {
        if (SAFE_METHODS.contains(request.getMethod())) {
            return false;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }
        for (Cookie cookie : cookies) {
            if (OAuth2CookieService.ACCESS_TOKEN_COOKIE.equals(cookie.getName())
                    || OAuth2CookieService.REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                return true;
            }
        }
        return false;
    }
}
