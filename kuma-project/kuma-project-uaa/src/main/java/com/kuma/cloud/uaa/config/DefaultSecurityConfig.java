package com.kuma.cloud.uaa.config;

import com.kuma.cloud.uaa.security.LoginPreCheckFilter;
import com.kuma.cloud.uaa.security.UaaAuthenticationFailureHandler;
import com.kuma.cloud.uaa.security.UaaAuthenticationSuccessHandler;
import com.kuma.cloud.uaa.service.MfaService;
import com.kuma.cloud.uaa.service.UaaUserService;
import com.kuma.cloud.uaa.support.LoginAttemptService;
import com.kuma.cloud.uaa.support.LoginAuditService;
import com.kuma.cloud.uaa.support.LoginCaptchaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import jakarta.servlet.DispatcherType;

/**
 * 登录页与管理 API 的安全配置。
 *
 * <p>管理 API 同时接受两种身份：浏览器控制台走表单登录后的 Session，外部系统走 UAA 自己签发的
 * Access Token；两者最终都归一为 GrantedAuthority，由方法级 {@code @PreAuthorize} 做鉴权。
 *
 * @author kuma
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class DefaultSecurityConfig {

    public static final String LOGIN_PAGE = "/login";

    public static final String LOGIN_PROCESSING_URL = "/login";

    private static final String[] PUBLIC_ENDPOINTS = {
        LOGIN_PAGE,
        "/captcha",
        "/error",
        "/favicon.ico",
        "/css/**",
        "/js/**",
        "/actuator/health",
        "/actuator/info",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/doc.html",
        "/webjars/**"
    };

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http,
            JwtDecoder jwtDecoder,
            UaaUserService userService,
            MfaService mfaService,
            LoginCaptchaService captchaService,
            LoginAttemptService attemptService,
            LoginAuditService auditService)
            throws Exception {
        UaaAuthenticationFailureHandler failureHandler =
                new UaaAuthenticationFailureHandler(LOGIN_PAGE, attemptService, auditService);
        LoginPreCheckFilter preCheckFilter = new LoginPreCheckFilter(
                LOGIN_PROCESSING_URL,
                captchaService,
                attemptService,
                mfaService,
                userService,
                failureHandler);

        http.csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        // Bearer Token 调用不依赖 Cookie，不存在 CSRF 面
                        .ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(authorize -> authorize
                        .dispatcherTypeMatchers(DispatcherType.ASYNC, DispatcherType.ERROR).permitAll()
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage(LOGIN_PAGE)
                        .loginProcessingUrl(LOGIN_PROCESSING_URL)
                        .successHandler(new UaaAuthenticationSuccessHandler(
                                userService, attemptService, auditService))
                        .failureHandler(failureHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl(LOGIN_PAGE + "?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .bearerTokenResolver(new DefaultBearerTokenResolver())
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .addFilterBefore(preCheckFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * 使用 DelegatingPasswordEncoder：密文自带 {bcrypt} 前缀，后续切换算法时旧密码仍可校验。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * 把 Access Token 的 authorities 声明还原为 GrantedAuthority，
     * 声明中已含 ROLE_ 前缀，故不再追加前缀。
     */
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("authorities");
        authoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }
}
