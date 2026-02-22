/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.NonNull
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.oauth2.core.OAuth2AccessToken
 *  org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.userdetails.converter;

import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

public interface AuthenticationToUserDetailsConverter {
    @NonNull
    public UserDetails convert(@NonNull AbstractOAuth2TokenAuthenticationToken<OAuth2AccessToken> var1);
}

