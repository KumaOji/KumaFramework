package com.kuma.cloud.blog.config;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientProperties;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientPropertiesMapper;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

/** Rebuilds client registrations and provider endpoints after a configuration refresh. */
@Configuration(proxyBeanMethods = false)
public class RefreshingOAuth2ClientConfiguration {
    @Bean
    @Primary
    @RefreshScope(proxyMode = ScopedProxyMode.INTERFACES)
    public InMemoryClientRegistrationRepository clientRegistrationRepository(Environment environment) {
        OAuth2ClientProperties properties = Binder.get(environment)
                .bind("spring.security.oauth2.client", Bindable.of(OAuth2ClientProperties.class))
                .orElseGet(OAuth2ClientProperties::new);
        return new InMemoryClientRegistrationRepository(
                new OAuth2ClientPropertiesMapper(properties).asClientRegistrations());
    }
}
