package com.kuma.cloud.blog.config;

import com.kuma.cloud.blog.security.JwtDenylistService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;

import java.util.List;

@Configuration
public class JwtConfig {

    @Bean
    public JwtDecoder blogJwtDecoder(
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri,
            @Value("${blog.oauth2.issuer-uri}") String issuerUri,
            @Value("${blog.oauth2.audience:blog}") String audience,
            JwtDenylistService denylistService) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
                .jwsAlgorithm(SignatureAlgorithm.RS256)
                .build();

        OAuth2TokenValidator<Jwt> issuerValidator =
                JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> audienceValidator =
                new JwtClaimValidator<List<String>>("aud",
                        audiences -> audiences != null && audiences.contains(audience));
        OAuth2TokenValidator<Jwt> revocationValidator = jwt ->
                denylistService.isRevoked(jwt)
                        ? OAuth2TokenValidatorResult.failure(
                                new OAuth2Error("invalid_token", "JWT 已撤销", null))
                        : OAuth2TokenValidatorResult.success();
        decoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<>(
                        issuerValidator, audienceValidator, revocationValidator));
        return decoder;
    }
}
