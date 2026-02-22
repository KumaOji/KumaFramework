/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.security.oauth2.client.endpoint.AbstractOAuth2AuthorizationGrantRequest
 *  org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient
 *  org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
 *  org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
 *  org.springframework.web.client.RestOperations
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client;

import com.kuma.boot.security.spring.authentication.login.social.oauth2client.weibo.WeiboOAuth2AccessTokenResponseClient;
import java.util.Collections;
import org.springframework.security.oauth2.client.endpoint.AbstractOAuth2AuthorizationGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.web.client.RestOperations;

public class SocialDelegateOAuth2AccessTokenResponseClient
implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {
    private final OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> delegate;
    private final RestOperations restOperations;

    public SocialDelegateOAuth2AccessTokenResponseClient(OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> delegate, RestOperations restOperations) {
        this.delegate = delegate;
        this.restOperations = restOperations;
    }

    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        String registrationId = authorizationGrantRequest.getClientRegistration().getRegistrationId();
        if (SocialClientProviders.WORK_WECHAT_SCAN_CLIENT.registrationId().equals(registrationId)) {
            OAuth2AccessTokenResponse tokenResponse = this.delegate.getTokenResponse((AbstractOAuth2AuthorizationGrantRequest)authorizationGrantRequest);
            String code = authorizationGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode();
            return OAuth2AccessTokenResponse.withResponse((OAuth2AccessTokenResponse)tokenResponse).additionalParameters(Collections.singletonMap("code", code)).build();
        }
        if ("weibo".equals(registrationId)) {
            return new WeiboOAuth2AccessTokenResponseClient(this.restOperations).getTokenResponse(authorizationGrantRequest);
        }
        return this.delegate.getTokenResponse((AbstractOAuth2AuthorizationGrantRequest)authorizationGrantRequest);
    }
}

