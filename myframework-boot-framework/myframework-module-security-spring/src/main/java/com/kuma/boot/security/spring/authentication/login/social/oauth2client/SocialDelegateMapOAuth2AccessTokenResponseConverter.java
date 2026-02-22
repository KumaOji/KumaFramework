/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.security.oauth2.core.OAuth2AccessToken$TokenType
 *  org.springframework.security.oauth2.core.endpoint.DefaultMapOAuth2AccessTokenResponseConverter
 *  org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client;

import java.util.Map;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.DefaultMapOAuth2AccessTokenResponseConverter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;

public class SocialDelegateMapOAuth2AccessTokenResponseConverter
implements Converter<Map<String, Object>, OAuth2AccessTokenResponse> {
    private final Converter<Map<String, Object>, OAuth2AccessTokenResponse> delegate = new DefaultMapOAuth2AccessTokenResponseConverter();

    public OAuth2AccessTokenResponse convert(Map<String, Object> tokenResponseParameters) {
        tokenResponseParameters.put("token_type", OAuth2AccessToken.TokenType.BEARER.getValue());
        return (OAuth2AccessTokenResponse)this.delegate.convert(tokenResponseParameters);
    }
}

