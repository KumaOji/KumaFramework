/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.core.type.TypeReference
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.core.ParameterizedTypeReference
 *  org.springframework.http.RequestEntity
 *  org.springframework.http.ResponseEntity
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.security.core.authority.SimpleGrantedAuthority
 *  org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler
 *  org.springframework.security.oauth2.client.registration.ClientRegistration
 *  org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
 *  org.springframework.security.oauth2.client.userinfo.OAuth2UserService
 *  org.springframework.security.oauth2.core.OAuth2AccessToken
 *  org.springframework.security.oauth2.core.user.OAuth2User
 *  org.springframework.security.oauth2.core.user.OAuth2UserAuthority
 *  org.springframework.web.client.ResponseErrorHandler
 *  org.springframework.web.client.RestOperations
 *  org.springframework.web.client.RestTemplate
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client.qq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuma.boot.common.utils.log.LogUtils;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class QQOauth2UserService
implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private static final String QQ_OPEN_ID_URL = "https://graph.qq.com/oauth2.0/me";
    private final RestOperations restOperations;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public QQOauth2UserService() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler((ResponseErrorHandler)new OAuth2ErrorResponseErrorHandler());
        this.restOperations = restTemplate;
    }

    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String registrationId = clientRegistration.getRegistrationId();
        String tokenValue = userRequest.getAccessToken().getTokenValue();
        RequestEntity openIdRequest = RequestEntity.get((URI)UriComponentsBuilder.fromUriString((String)QQ_OPEN_ID_URL).queryParam("access_token", new Object[]{tokenValue}).build().toUri()).build();
        ResponseEntity openIdResponse = this.restOperations.exchange(openIdRequest, (ParameterizedTypeReference)new ParameterizedTypeReference<String>(this){});
        LogUtils.info((String)"qq\u7684openId\u54cd\u5e94\u4fe1\u606f\uff1a{}", (Object[])new Object[]{openIdResponse});
        String openId = null;
        try {
            openId = this.extractQqOpenId(Objects.requireNonNull((String)openIdResponse.getBody()));
        }
        catch (JsonProcessingException e) {
            LogUtils.error((Throwable)e);
        }
        RequestEntity userInfoRequest = RequestEntity.get((URI)UriComponentsBuilder.fromUriString((String)clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri()).queryParam("access_token", new Object[]{tokenValue}).queryParam("openid", new Object[]{openId}).queryParam("oauth_consumer_key", new Object[]{clientRegistration.getClientId()}).build().toUri()).build();
        ResponseEntity userInfoResponse = this.restOperations.exchange(userInfoRequest, (ParameterizedTypeReference)new ParameterizedTypeReference<String>(this){});
        LogUtils.info((String)"qq\u7684userInfo\u54cd\u5e94\u4fe1\u606f\uff1a{}", (Object[])new Object[]{userInfoResponse});
        String userNameAttributeName = clientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> userAttributes = null;
        try {
            userAttributes = this.extractQqUserInfo(Objects.requireNonNull((String)userInfoResponse.getBody()));
        }
        catch (JsonProcessingException e) {
            LogUtils.error((Throwable)e);
        }
        LinkedHashSet<GrantedAuthority> authorities = new LinkedHashSet<GrantedAuthority>();
        authorities.add((GrantedAuthority)new OAuth2UserAuthority(userAttributes));
        OAuth2AccessToken token = userRequest.getAccessToken();
        for (String authority : token.getScopes()) {
            authorities.add((GrantedAuthority)new SimpleGrantedAuthority("SCOPE_" + authority));
        }
        QQOAuth2User qqoAuth2User = (QQOAuth2User)this.objectMapper.convertValue(userAttributes, QQOAuth2User.class);
        qqoAuth2User.setAttributes(userAttributes);
        qqoAuth2User.setAuthorities(authorities);
        qqoAuth2User.setNameAttributeKey(userNameAttributeName);
        return qqoAuth2User;
    }

    private String extractQqOpenId(String openIdResponse) throws JsonProcessingException {
        String openId = openIdResponse.substring(openIdResponse.indexOf(40) + 1, openIdResponse.indexOf(41));
        Map map = (Map)this.objectMapper.readValue(openId, (TypeReference)new TypeReference<Map<String, String>>(this){});
        return (String)map.get("openid");
    }

    private Map<String, Object> extractQqUserInfo(String userInfoResponse) throws JsonProcessingException {
        return (Map)this.objectMapper.readValue(userInfoResponse, (TypeReference)new TypeReference<Map<String, Object>>(this){});
    }
}

