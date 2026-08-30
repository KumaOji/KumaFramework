package com.kuma.cloud.uaa.config;

import com.kuma.boot.security.spring.authentication.response.denied.JsonAccessDeniedHandler;
import com.kuma.boot.security.spring.authentication.response.entrypoint.JsonAuthenticationEntryPoint;
import com.kuma.boot.security.spring.autoconfigure.properties.SecurityProperties;
import com.kuma.boot.security.spring.oauth2.authentication.SecurityJwtGrantedAuthoritiesConverter;
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
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;

import static com.kuma.boot.security.spring.utils.SecurityUtils.toRequestMatchers;

/**
 * 登录页与管理 API 的安全配置。
 *
 * <p>白名单走框架 {@link SecurityProperties#getIgnoreUrl()}；管理 API 鉴权走
 * {@link com.kuma.boot.security.spring.access.expression.Authorize}；JWT 声明解析复用
 * {@link SecurityJwtGrantedAuthoritiesConverter}。
 */
@Configuration
@EnableWebSecurity
public class DefaultSecurityConfig {

    public static final String LOGIN_PAGE = "/login";

    public static final String LOGIN_PROCESSING_URL = "/login";

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http,
            JwtDecoder jwtDecoder,
            SecurityProperties securityProperties,
            UaaProperties properties,
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
                properties,
                captchaService,
                attemptService,
                mfaService,
                userService,
                failureHandler);

        CsrfTokenRequestAttributeHandler csrfHandler = new CsrfTokenRequestAttributeHandler();
        csrfHandler.setCsrfRequestAttributeName(null);

        JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint = new JsonAuthenticationEntryPoint();
        JsonAccessDeniedHandler jsonAccessDeniedHandler = new JsonAccessDeniedHandler();
        MediaTypeRequestMatcher jsonApiMatcher = new MediaTypeRequestMatcher(
                org.springframework.http.MediaType.APPLICATION_JSON,
                org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON);

        http.csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(csrfHandler)
                        .ignoringRequestMatchers(DefaultSecurityConfig::isBearerTokenRequest))
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31_536_000)))
                .authorizeHttpRequests(authorize -> authorize
                        .dispatcherTypeMatchers(DispatcherType.ASYNC, DispatcherType.ERROR).permitAll()
                        .requestMatchers(toRequestMatchers(securityProperties.getIgnoreUrl())).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                jsonAuthenticationEntryPoint,
                                new OrRequestMatcher(
                                        new NegatedRequestMatcher(new MediaTypeRequestMatcher(
                                                org.springframework.http.MediaType.TEXT_HTML)),
                                        jsonApiMatcher))
                        .defaultAccessDeniedHandlerFor(
                                jsonAccessDeniedHandler,
                                new OrRequestMatcher(
                                        new NegatedRequestMatcher(new MediaTypeRequestMatcher(
                                                org.springframework.http.MediaType.TEXT_HTML)),
                                        jsonApiMatcher)))
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
                        .deleteCookies("UAA_SESSION", "JSESSIONID"))
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .bearerTokenResolver(new DefaultBearerTokenResolver())
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .addFilterBefore(preCheckFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private static boolean isBearerTokenRequest(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        return authorization != null && authorization.regionMatches(true, 0, "Bearer ", 0, 7);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        SecurityJwtGrantedAuthoritiesConverter authoritiesConverter =
                new SecurityJwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("authorities");
        authoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }
}
