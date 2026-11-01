package com.kuma.cloud.gateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.reactive.JwkSetUriReactiveJwtDecoderBuilderCustomizer;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.security.interfaces.RSAPublicKey;
import org.springframework.security.converter.RsaKeyConverters;

/** Keeps the resource-server verifier in step with refreshed issuer and JWK endpoints. */
@Configuration(proxyBeanMethods = false)
public class RefreshingJwtConfiguration {
    @Bean
    @Lazy
    @ConditionalOnMissingBean(ReactiveJwtDecoder.class)
    @RefreshScope(proxyMode = ScopedProxyMode.INTERFACES)
    public ReactiveJwtDecoder gatewayJwtDecoder(Environment environment,
            ObjectProvider<OAuth2TokenValidator<Jwt>> additionalValidators,
            ObjectProvider<JwkSetUriReactiveJwtDecoderBuilderCustomizer> customizers) throws java.io.IOException {
        var properties = Binder.get(environment)
                .bind("spring.security.oauth2.resourceserver", Bindable.of(OAuth2ResourceServerProperties.class))
                .orElseGet(OAuth2ResourceServerProperties::new).getJwt();
        String jwkSetUri = properties.getJwkSetUri();
        String issuer = properties.getIssuerUri();
        NimbusReactiveJwtDecoder decoder;
        if (StringUtils.hasText(jwkSetUri) || StringUtils.hasText(issuer)) {
            var builder = StringUtils.hasText(jwkSetUri)
                    ? NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri)
                    : NimbusReactiveJwtDecoder.withIssuerLocation(issuer);
            builder.jwsAlgorithms(algorithms -> properties.getJwsAlgorithms()
                    .forEach(name -> algorithms.add(SignatureAlgorithm.from(name))));
            customizers.orderedStream().forEach(customizer -> customizer.customize(builder));
            decoder = builder.build();
        } else {
            Assert.state(properties.getPublicKeyLocation() != null,
                    "Gateway JWT authentication requires jwk-set-uri, issuer-uri or public-key-location");
            Assert.state(properties.getJwsAlgorithms().size() == 1,
                    "A public-key JWT decoder requires exactly one JWS algorithm");
            try (var input = properties.getPublicKeyLocation().getInputStream()) {
                RSAPublicKey publicKey = RsaKeyConverters.x509().convert(input);
                decoder = NimbusReactiveJwtDecoder.withPublicKey(publicKey)
                        .signatureAlgorithm(SignatureAlgorithm.from(properties.getJwsAlgorithms().getFirst())).build();
            }
        }
        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        if (StringUtils.hasText(issuer)) validators.add(new JwtIssuerValidator(issuer));
        if (!properties.getAudiences().isEmpty()) {
            validators.add(new JwtClaimValidator<List<String>>("aud",
                    audiences -> audiences != null && !Collections.disjoint(audiences, properties.getAudiences())));
        }
        additionalValidators.orderedStream().forEach(validators::add);
        decoder.setJwtValidator(JwtValidators.createDefaultWithValidators(validators));
        return decoder;
    }
}
