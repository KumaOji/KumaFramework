/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler
 *  org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
 *  org.springframework.security.oauth2.client.userinfo.OAuth2UserService
 *  org.springframework.security.oauth2.core.OAuth2AuthenticationException
 *  org.springframework.security.oauth2.core.user.OAuth2User
 *  org.springframework.web.client.ResponseErrorHandler
 *  org.springframework.web.client.RestOperations
 *  org.springframework.web.client.RestTemplate
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client.weibo;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

public class WeiboOAuth2UserService
implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final RestOperations restOperations;

    public WeiboOAuth2UserService() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler((ResponseErrorHandler)new OAuth2ErrorResponseErrorHandler());
        this.restOperations = restTemplate;
    }

    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        Map additionalParameters = oAuth2UserRequest.getAdditionalParameters();
        String uid = additionalParameters.get("uid").toString();
        String accessToken = oAuth2UserRequest.getAccessToken().getTokenValue();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", uid);
        params.put("access_token", accessToken);
        String baseUri = oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();
        String userInfoUri = baseUri + "?uid={uid}&access_token={access_token}";
        WeiboOAuth2User weiboOAuth2User = (WeiboOAuth2User)this.restOperations.getForObject(userInfoUri, WeiboOAuth2User.class, params);
        weiboOAuth2User.setNameAttributeKey(weiboOAuth2User.getIdstr());
        return weiboOAuth2User;
    }
}

