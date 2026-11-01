package com.kuma.cloud.gateway.config;

import com.kuma.cloud.gateway.properties.GatewayCloudProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import java.util.Map;

import static org.mockito.Mockito.mock;

class GatewayRefreshTest {
    @Test
    void ipFilterCanBeEnabledAfterStartingWithTheGatewayDisabled() {
        new ReactiveWebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(RefreshAutoConfiguration.class))
                .withUserConfiguration(Fixtures.class, GatewayFilterConfiguration.class)
                .withPropertyValues("kuma.cloud.gateway.enabled=false", "kuma.cloud.gateway.access.enabled=false")
                .run(context -> {
                    var filter = context.getBean(com.kuma.cloud.gateway.filter.ClientAccessGlobalFilter.class);
                    var continued = new java.util.concurrent.atomic.AtomicInteger();
                    org.springframework.cloud.gateway.filter.GatewayFilterChain chain = exchange -> {
                        continued.incrementAndGet();
                        return reactor.core.publisher.Mono.empty();
                    };
                    var before = ipExchange();
                    filter.filter(before, chain).block();
                    org.assertj.core.api.Assertions.assertThat(continued.get()).isEqualTo(1);

                    var values = new java.util.HashMap<String, Object>();
                    values.put("kuma.cloud.gateway.enabled", true);
                    values.put("kuma.cloud.gateway.access.enabled", true);
                    values.put("kuma.cloud.gateway.access.black-list[0]", "192.0.2.1");
                    context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("runtime", values));
                    refresh(context, values);
                    var blocked = ipExchange();
                    filter.filter(blocked, chain).block();
                    org.assertj.core.api.Assertions.assertThat(blocked.getResponse().getStatusCode())
                            .isEqualTo(org.springframework.http.HttpStatus.FORBIDDEN);
                    org.assertj.core.api.Assertions.assertThat(continued.get()).isEqualTo(1);

                    values.put("kuma.cloud.gateway.access.black-list[0]", "192.0.2.2");
                    refresh(context, values);
                    filter.filter(ipExchange(), chain).block();
                    org.assertj.core.api.Assertions.assertThat(continued.get()).isEqualTo(2);
                });
    }

    private org.springframework.mock.web.server.MockServerWebExchange ipExchange() {
        return org.springframework.mock.web.server.MockServerWebExchange.from(
                org.springframework.mock.http.server.reactive.MockServerHttpRequest.get("http://localhost/private")
                        .remoteAddress(new java.net.InetSocketAddress("192.0.2.1", 12345)));
    }

    @Test
    void refreshChangesAuthenticationWhitelistAndCorsWithoutRestarting() {
        new ReactiveWebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(RefreshAutoConfiguration.class))
                .withUserConfiguration(Fixtures.class, GatewaySecurityConfiguration.class, GatewayConfiguration.class)
                .withPropertyValues("kuma.cloud.gateway.enabled=false", "kuma.cloud.gateway.auth.enabled=false")
                .run(context -> {
                    WebTestClient client = WebTestClient.bindToApplicationContext(context)
                            .configureClient().baseUrl("http://localhost").build();
                    client.get().uri("/private").exchange().expectStatus().isOk();

                    var values = new java.util.HashMap<String, Object>();
                    values.put("kuma.cloud.gateway.enabled", true);
                    values.put("kuma.cloud.gateway.auth.enabled", true);
                    values.put("kuma.cloud.gateway.cors.allowed-origin-patterns[0]", "https://old.example");
                    context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("runtime", values));
                    refresh(context, values);
                    client.get().uri("/private").exchange().expectStatus().isUnauthorized();
                    client.options().uri("/private").header(HttpHeaders.ORIGIN, "https://old.example")
                            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                            .exchange().expectStatus().isOk()
                            .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://old.example");

                    values.put("kuma.cloud.gateway.auth.white-list[0]", "/private");
                    values.put("kuma.cloud.gateway.cors.allowed-origin-patterns[0]", "https://new.example");
                    refresh(context, values);
                    org.assertj.core.api.Assertions.assertThat(context.getBean(GatewayCloudProperties.class)
                            .getCors().getAllowedOriginPatterns()).containsExactly("https://new.example");
                    client.get().uri("/private").exchange().expectStatus().isOk();
                    client.get().uri("/private").header(HttpHeaders.ORIGIN, "https://old.example")
                            .exchange().expectStatus().isForbidden();
                    client.get().uri("/private").header(HttpHeaders.ORIGIN, "https://new.example")
                            .exchange().expectStatus().isOk()
                            .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://new.example");

                    values.put("kuma.cloud.gateway.auth.white-list[0]", "/other");
                    refresh(context, values);
                    client.get().uri("/private").exchange().expectStatus().isUnauthorized();
                    values.put("kuma.cloud.gateway.auth.enabled", false);
                    refresh(context, values);
                    client.get().uri("/private").exchange().expectStatus().isOk();
                });
    }

    private void refresh(org.springframework.context.ConfigurableApplicationContext context, Map<String, Object> values) {
        context.publishEvent(new EnvironmentChangeEvent(values.keySet()));
        context.getBean(RefreshScope.class).refreshAll();
    }

    @Configuration(proxyBeanMethods = false)
    @EnableWebFlux
    @EnableConfigurationProperties(GatewayCloudProperties.class)
    static class Fixtures {
        @Bean ReactiveJwtDecoder jwtDecoder() { return mock(ReactiveJwtDecoder.class); }
        @Bean Endpoint endpoint() { return new Endpoint(); }
    }

    @RestController
    static class Endpoint {
        @GetMapping("/private") String content() { return "ok"; }
    }
}
