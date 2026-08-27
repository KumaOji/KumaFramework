package com.kuma.cloud.uaa.config;

import com.kuma.boot.security.spring.constants.DefaultConstants;
import com.kuma.cloud.uaa.security.UaaOidcUserInfoMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

/**
 * OAuth2.1 / OIDC 协议端点配置。
 *
 * <p>端点路径全部采用 Spring Authorization Server 默认值，与
 * {@code com.kuma.boot.security.spring.constants.DefaultConstants} 中框架侧约定的端点一致：
 * {@code /oauth2/authorize}、{@code /oauth2/token}、{@code /oauth2/jwks}、
 * {@code /oauth2/revoke}、{@code /oauth2/introspect}、{@code /userinfo}。
 *
 * @author kuma
 */
@Configuration
public class AuthorizationServerConfig {

    /**
     * 授权确认页路径，与框架 {@link DefaultConstants#AUTHORIZATION_CONSENT_URI} 保持一致。
     */
    public static final String CONSENT_PAGE = DefaultConstants.AUTHORIZATION_CONSENT_URI;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http, JwtDecoder jwtDecoder, UaaOidcUserInfoMapper userInfoMapper)
            throws Exception {
        http.oauth2AuthorizationServer(authorizationServer -> {
                    http.securityMatcher(authorizationServer.getEndpointsMatcher());
                    authorizationServer
                            .authorizationEndpoint(
                                    authorizationEndpoint -> authorizationEndpoint.consentPage(CONSENT_PAGE))
                            .oidc(oidc -> oidc.userInfoEndpoint(
                                    userInfo -> userInfo.userInfoMapper(userInfoMapper)));
                })
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                // 浏览器访问 /oauth2/authorize 未登录时跳登录页；/userinfo 等接口调用则返回 401
                .exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint(DefaultSecurityConfig.LOGIN_PAGE),
                        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
                // /userinfo 与 /connect/logout 需要用 UAA 自己签发的 Access Token 认证
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .bearerTokenResolver(new DefaultBearerTokenResolver())
                        .jwt(jwt -> jwt.decoder(jwtDecoder)));
        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    /**
     * 授权记录持久化到数据库，UAA 重启或多实例部署后授权码与 Refresh Token 仍然有效。
     */
    @Bean
    public OAuth2AuthorizationService authorizationService(
            JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(
            JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings(UaaProperties properties) {
        return AuthorizationServerSettings.builder().issuer(properties.getIssuer()).build();
    }
}
