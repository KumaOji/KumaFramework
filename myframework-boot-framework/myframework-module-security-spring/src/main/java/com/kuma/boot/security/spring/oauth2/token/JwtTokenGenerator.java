/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
 *  org.springframework.security.oauth2.core.user.OAuth2User
 */
package com.kuma.boot.security.spring.oauth2.token;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface JwtTokenGenerator {
    public OAuth2AccessTokenResponse tokenResponse(UserDetails var1);

    public OAuth2AccessTokenResponse socialTokenResponse(OAuth2User var1);
}

