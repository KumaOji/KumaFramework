/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson2.JSONObject
 *  org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient
 *  org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
 *  org.springframework.security.oauth2.client.registration.ClientRegistration
 *  org.springframework.security.oauth2.core.OAuth2AccessToken$TokenType
 *  org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
 *  org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange
 *  org.springframework.web.client.RestOperations
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client.weibo;

import com.alibaba.fastjson2.JSONObject;
import java.util.HashMap;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.web.client.RestOperations;

public class WeiboOAuth2AccessTokenResponseClient
implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {
    private final RestOperations restOperations;

    public WeiboOAuth2AccessTokenResponseClient(RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest oAuth2AuthorizationCodeGrantRequest) {
        ClientRegistration clientRegistration = oAuth2AuthorizationCodeGrantRequest.getClientRegistration();
        OAuth2AuthorizationExchange oAuth2AuthorizationExchange = oAuth2AuthorizationCodeGrantRequest.getAuthorizationExchange();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("client_id", clientRegistration.getClientId());
        params.put("client_secret", clientRegistration.getClientSecret());
        params.put("grant_type", clientRegistration.getAuthorizationGrantType().getValue());
        params.put("code", oAuth2AuthorizationExchange.getAuthorizationResponse().getCode());
        params.put("redirect_uri", oAuth2AuthorizationExchange.getAuthorizationResponse().getRedirectUri());
        String baseUri = clientRegistration.getProviderDetails().getTokenUri();
        String accessTokenUri = baseUri + "?client_id={client_id}&client_secret={client_secret}&grant_type={grant_type}&redirect_uri={redirect_uri}&code={code}";
        String accessTokenResponse = (String)this.restOperations.postForObject(accessTokenUri, null, String.class, params);
        JSONObject object = JSONObject.parseObject((String)accessTokenResponse);
        String accessToken = object.getString("access_token");
        String expiresIn = object.getString("expires_in");
        String uid = object.getString("uid");
        HashMap<String, String> additionalParameters = new HashMap<String, String>();
        additionalParameters.put("uid", uid);
        return OAuth2AccessTokenResponse.withToken((String)accessToken).expiresIn(Long.parseLong(expiresIn)).tokenType(OAuth2AccessToken.TokenType.BEARER).scopes(oAuth2AuthorizationExchange.getAuthorizationRequest().getScopes()).additionalParameters(additionalParameters).build();
    }
}

