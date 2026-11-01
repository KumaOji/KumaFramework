package com.kuma.cloud.blog.config;

import com.kuma.cloud.blog.security.OAuth2CookieService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.core.env.MapPropertySource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import java.time.Instant;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class BlogRefreshTest {
    @Test
    void oauth2LoginSecurityChainStartsWithARefreshableClientRepository() {
        new org.springframework.boot.test.context.runner.WebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(RefreshAutoConfiguration.class))
                .withUserConfiguration(RefreshingOAuth2ClientConfiguration.class, OAuth2LoginTestConfiguration.class)
                .withPropertyValues(
                        "spring.security.oauth2.client.registration.blog.provider=uaa",
                        "spring.security.oauth2.client.registration.blog.client-id=blog",
                        "spring.security.oauth2.client.registration.blog.client-secret=secret",
                        "spring.security.oauth2.client.registration.blog.authorization-grant-type=authorization_code",
                        "spring.security.oauth2.client.registration.blog.redirect-uri=http://localhost/callback",
                        "spring.security.oauth2.client.provider.uaa.authorization-uri=http://localhost/authorize",
                        "spring.security.oauth2.client.provider.uaa.token-uri=http://localhost/token")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(org.springframework.security.web.SecurityFilterChain.class);
                    var repository = context.getBean(ClientRegistrationRepository.class);
                    assertThat(org.springframework.core.ResolvableType.forInstance(repository)
                            .as(Iterable.class).resolveGeneric())
                            .isEqualTo(org.springframework.security.oauth2.client.registration.ClientRegistration.class);
                });
    }

    @org.springframework.context.annotation.Configuration(proxyBeanMethods = false)
    @org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
    static class OAuth2LoginTestConfiguration {
        @org.springframework.context.annotation.Bean
        org.springframework.security.web.SecurityFilterChain loginChain(
                org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
            return http.oauth2Login(org.springframework.security.config.Customizer.withDefaults()).build();
        }
    }

    @Test
    void refreshUpdatesTheExistingMvcAsyncAdapter() {
        var adapter = org.mockito.Mockito.mock(
                org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.class);
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(RefreshAutoConfiguration.class))
                .withUserConfiguration(WebMvcConfig.class)
                .withBean(org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.class, () -> adapter)
                .run(context -> {
                    context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("runtime",
                            java.util.Map.of("spring.mvc.async.request-timeout", 120000)));
                    context.getBean(RefreshScope.class).refreshAll();
                    org.mockito.Mockito.verify(adapter).setAsyncRequestTimeout(120000L);
                });
    }

    @Test
    void existingConsumersSeeNewCookieSettingsAndOAuthClientRegistration() {
        new ApplicationContextRunner()
                .withInitializer(context -> context.getBeanFactory().setConversionService(
                        org.springframework.boot.convert.ApplicationConversionService.getSharedInstance()))
                .withConfiguration(AutoConfigurations.of(RefreshAutoConfiguration.class))
                .withUserConfiguration(OAuth2CookieService.class, RefreshingOAuth2ClientConfiguration.class)
                .withPropertyValues(
                        "blog.cookie-secure=false", "blog.oauth2.refresh-token-max-age=1d",
                        "spring.security.oauth2.client.registration.blog.provider=uaa",
                        "spring.security.oauth2.client.registration.blog.client-id=blog",
                        "spring.security.oauth2.client.registration.blog.client-secret=old-secret",
                        "spring.security.oauth2.client.registration.blog.authorization-grant-type=authorization_code",
                        "spring.security.oauth2.client.registration.blog.redirect-uri=http://localhost/callback",
                        "spring.security.oauth2.client.provider.uaa.authorization-uri=http://localhost/authorize",
                        "spring.security.oauth2.client.provider.uaa.token-uri=http://localhost/token")
                .run(context -> {
                    OAuth2CookieService cookies = context.getBean(OAuth2CookieService.class);
                    ClientRegistrationRepository registrations = context.getBean(ClientRegistrationRepository.class);
                    assertThat(registrations.findByRegistrationId("blog").getClientSecret()).isEqualTo("old-secret");
                    var before = new MockHttpServletResponse();
                    cookies.writeTokens(before, "access", Instant.now().plusSeconds(300), "refresh");
                    assertThat(before.getHeaders("Set-Cookie")).anyMatch(value -> value.contains("Max-Age=86400"));

                    var changed = new HashMap<String, Object>();
                    changed.put("blog.cookie-secure", true);
                    changed.put("blog.oauth2.refresh-token-max-age", "2d");
                    changed.put("spring.security.oauth2.client.registration.blog.client-secret", "new-secret");
                    changed.put("spring.security.oauth2.client.provider.uaa.token-uri", "http://localhost/new-token");
                    context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("runtime", changed));
                    context.getBean(RefreshScope.class).refreshAll();

                    var after = new MockHttpServletResponse();
                    cookies.writeTokens(after, "access", Instant.now().plusSeconds(300), "refresh");
                    assertThat(after.getHeaders("Set-Cookie")).allMatch(value -> value.contains("Secure"));
                    assertThat(after.getHeaders("Set-Cookie")).anyMatch(value -> value.contains("Max-Age=172800"));
                    var registration = registrations.findByRegistrationId("blog");
                    assertThat(registration.getClientSecret()).isEqualTo("new-secret");
                    assertThat(registration.getProviderDetails().getTokenUri()).isEqualTo("http://localhost/new-token");
                });
    }
}
