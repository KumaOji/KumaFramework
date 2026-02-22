/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest$Builder
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client;

import java.util.Arrays;
import java.util.Objects;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

public class SocialOAuth2AuthorizationRequestCustomizer {
    public static void customize(OAuth2AuthorizationRequest.Builder builder) {
        builder.attributes(attributes -> Arrays.stream(SocialClientProviders.values()).filter(clientProvider -> Objects.equals(clientProvider.registrationId(), attributes.get("registration_id"))).findAny().map(SocialClientProviders::requestConsumer).ifPresent(requestConsumer -> requestConsumer.accept(builder)));
    }
}

