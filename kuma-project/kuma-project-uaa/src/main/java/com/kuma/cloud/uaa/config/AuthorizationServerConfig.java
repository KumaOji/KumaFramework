package com.kuma.cloud.uaa.config;

import com.kuma.cloud.uaa.security.UaaOidcUserInfoMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.consent.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.consent.OAuth2AuthorizationConsentService;
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
     * 授权确认页路径。第三方客户端开启 requireAuthorizationConsent 后由 SAS 重定向至此。
     */
    public static final String CONSENT_PAGE = "/oauth2/consent";

    @Bean
    @Order(1)
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
