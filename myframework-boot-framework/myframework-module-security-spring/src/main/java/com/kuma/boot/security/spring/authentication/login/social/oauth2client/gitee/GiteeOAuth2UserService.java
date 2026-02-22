/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.http.HttpEntity
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpMethod
 *  org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler
 *  org.springframework.security.oauth2.client.registration.ClientRegistration
 *  org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
 *  org.springframework.security.oauth2.client.userinfo.OAuth2UserService
 *  org.springframework.security.oauth2.core.OAuth2AuthenticationException
 *  org.springframework.security.oauth2.core.user.OAuth2User
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.client.ResponseErrorHandler
 *  org.springframework.web.client.RestOperations
 *  org.springframework.web.client.RestTemplate
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client.gitee;

import java.util.HashMap;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

public class GiteeOAuth2UserService
implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final RestOperations restOperations;

    public GiteeOAuth2UserService() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler((ResponseErrorHandler)new OAuth2ErrorResponseErrorHandler());
        this.restOperations = restTemplate;
    }

    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration = oAuth2UserRequest.getClientRegistration();
        String userNameAttributeName = clientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        String accessToken = oAuth2UserRequest.getAccessToken().getTokenValue();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("access_token", accessToken);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
        HttpEntity entity = new HttpEntity((MultiValueMap)httpHeaders);
        String url = oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri() + "?access_token={access_token}";
        GiteeOAuth2User giteeOAuth2User = (GiteeOAuth2User)this.restOperations.exchange(url, HttpMethod.GET, entity, GiteeOAuth2User.class, params).getBody();
        giteeOAuth2User.setNameAttributeKey(userNameAttributeName);
        return giteeOAuth2User;
    }
}

