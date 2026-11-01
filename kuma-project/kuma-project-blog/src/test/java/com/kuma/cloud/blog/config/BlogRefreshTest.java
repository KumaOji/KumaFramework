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
