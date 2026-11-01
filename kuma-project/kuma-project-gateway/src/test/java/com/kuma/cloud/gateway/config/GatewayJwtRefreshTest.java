package com.kuma.cloud.gateway.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.core.env.MapPropertySource;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GatewayJwtRefreshTest {
    @Test
    void existingDecoderReferenceUsesNewJwkEndpointIssuerAndAudience() throws Exception {
        RSAKey oldKey = new RSAKeyGenerator(2048).keyID("old").generate();
        RSAKey newKey = new RSAKeyGenerator(2048).keyID("new").generate();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        endpoint(server, "/old", oldKey);
        endpoint(server, "/new", newKey);
        server.start();
        try {
            String address = "http://127.0.0.1:" + server.getAddress().getPort();
            new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(RefreshAutoConfiguration.class))
                    .withUserConfiguration(RefreshingJwtConfiguration.class)
                    .withPropertyValues(
                            "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=" + address + "/old",
                            "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://old.example",
                            "spring.security.oauth2.resourceserver.jwt.audiences[0]=old-audience")
                    .run(context -> {
                        ReactiveJwtDecoder decoder = context.getBean(ReactiveJwtDecoder.class);
                        String oldToken = token(oldKey, "https://old.example", "old-audience");
                        assertThat(decoder.decode(oldToken).block(Duration.ofSeconds(5))).isNotNull();

                        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("runtime", Map.of(
                                "spring.security.oauth2.resourceserver.jwt.jwk-set-uri", address + "/new",
                                "spring.security.oauth2.resourceserver.jwt.issuer-uri", "https://new.example",
                                "spring.security.oauth2.resourceserver.jwt.audiences[0]", "new-audience")));
                        context.getBean(RefreshScope.class).refreshAll();
                        assertThat(decoder.decode(token(newKey, "https://new.example", "new-audience"))
                                .block(Duration.ofSeconds(5))).isNotNull();
                        assertThatThrownBy(() -> decoder.decode(oldToken).block(Duration.ofSeconds(5)))
                                .isInstanceOf(org.springframework.security.oauth2.jwt.JwtException.class);
                        assertThatThrownBy(() -> decoder.decode(token(newKey, "https://old.example", "new-audience"))
                                .block(Duration.ofSeconds(5))).isInstanceOf(org.springframework.security.oauth2.jwt.JwtException.class);
                        assertThatThrownBy(() -> decoder.decode(token(newKey, "https://new.example", "old-audience"))
                                .block(Duration.ofSeconds(5))).isInstanceOf(org.springframework.security.oauth2.jwt.JwtException.class);
                    });
        } finally {
            server.stop(0);
        }
    }

    private void endpoint(HttpServer server, String path, RSAKey key) {
        server.createContext(path, exchange -> {
            byte[] bytes = new JWKSet(key.toPublicJWK()).toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            try (var output = exchange.getResponseBody()) { output.write(bytes); }
        });
    }

    private String token(RSAKey key, String issuer, String audience) throws Exception {
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(key.getKeyID()).build(),
                new JWTClaimsSet.Builder().issuer(issuer).subject("user").audience(audience)
                        .issueTime(Date.from(Instant.now())).expirationTime(Date.from(Instant.now().plusSeconds(300))).build());
        jwt.sign(new RSASSASigner(key));
        return jwt.serialize();
    }
}
