/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.nimbusds.oauth2.sdk.GrantType
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.http.RequestEntity
 *  org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest
 *  org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequestEntityConverter
 *  org.springframework.security.oauth2.client.registration.ClientRegistration
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client;

import com.nimbusds.oauth2.sdk.GrantType;

import java.net.URI;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public class SocialDelegateOAuth2RefreshTokenRequestEntityConverter
implements Converter<OAuth2RefreshTokenGrantRequest, RequestEntity<?>> {
    private static final String REFRESH_TOKEN_ENDPOINT = "https://api.weixin.qq.com/sns/oauth2/refresh_token";
    private final Converter<OAuth2RefreshTokenGrantRequest, RequestEntity<?>> requestEntityConverter = new OAuth2RefreshTokenGrantRequestEntityConverter();

    public RequestEntity<?> convert(OAuth2RefreshTokenGrantRequest source) {
        ClientRegistration clientRegistration = source.getClientRegistration();
        if (SocialClientProviders.WECHAT_WEB_LOGIN_CLIENT.registrationId().equals(clientRegistration.getClientId())) {
            LinkedMultiValueMap queryParameters = new LinkedMultiValueMap();
            queryParameters.add((Object)"appid", (Object)clientRegistration.getClientId());
            queryParameters.add((Object)"grant_type", (Object)GrantType.REFRESH_TOKEN.getValue());
            queryParameters.add((Object)"refresh_token", (Object)source.getRefreshToken().getTokenValue());
            URI uri = UriComponentsBuilder.fromUriString((String)REFRESH_TOKEN_ENDPOINT).queryParams((MultiValueMap)queryParameters).build().toUri();
            return RequestEntity.get((URI)uri).build();
        }
        return (RequestEntity)this.requestEntityConverter.convert((Object)source);
    }
}

