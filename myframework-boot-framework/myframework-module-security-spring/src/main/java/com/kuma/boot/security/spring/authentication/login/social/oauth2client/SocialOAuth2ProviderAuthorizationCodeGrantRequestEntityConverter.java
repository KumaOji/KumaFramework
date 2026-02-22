/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.http.RequestEntity
 *  org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
 *  org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter
 *  org.springframework.security.oauth2.client.registration.ClientRegistration
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client;

import java.util.Arrays;
import java.util.Objects;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

public class SocialOAuth2ProviderAuthorizationCodeGrantRequestEntityConverter
implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {
    private final Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> defaultConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();

    public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
        ClientRegistration clientRegistration = authorizationCodeGrantRequest.getClientRegistration();
        String registrationId = clientRegistration.getRegistrationId();
        return (RequestEntity)Arrays.stream(SocialClientProviders.values()).filter(clientProvider -> Objects.equals(clientProvider.registrationId(), registrationId)).findAny().map(SocialClientProviders::converter).orElse(this.defaultConverter).convert((Object)authorizationCodeGrantRequest);
    }
}

