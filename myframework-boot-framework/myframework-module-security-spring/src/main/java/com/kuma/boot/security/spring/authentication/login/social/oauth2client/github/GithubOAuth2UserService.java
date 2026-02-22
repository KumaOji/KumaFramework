/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  org.springframework.security.oauth2.client.registration.ClientRegistration
 *  org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
 *  org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
 *  org.springframework.security.oauth2.core.OAuth2AuthenticationException
 *  org.springframework.security.oauth2.core.user.OAuth2User
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client.github;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class GithubOAuth2UserService
extends DefaultOAuth2UserService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration = oAuth2UserRequest.getClientRegistration();
        String registrationId = clientRegistration.getRegistrationId();
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        Map attributes = oAuth2User.getAttributes();
        String userNameAttributeName = clientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        GithubOAuth2User githubOAuth2User = (GithubOAuth2User)this.objectMapper.convertValue((Object)attributes, GithubOAuth2User.class);
        githubOAuth2User.setNameAttributeKey(userNameAttributeName);
        return githubOAuth2User;
    }
}

