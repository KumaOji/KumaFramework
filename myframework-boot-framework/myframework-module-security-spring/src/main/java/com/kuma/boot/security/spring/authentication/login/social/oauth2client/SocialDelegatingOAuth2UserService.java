/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
 *  org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
 *  org.springframework.security.oauth2.client.userinfo.OAuth2UserService
 *  org.springframework.security.oauth2.core.OAuth2AuthenticationException
 *  org.springframework.security.oauth2.core.user.OAuth2User
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

public class SocialDelegatingOAuth2UserService<R extends OAuth2UserRequest, U extends OAuth2User>
implements OAuth2UserService<R, U> {
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> defaultOAuth2UserService = new DefaultOAuth2UserService();
    private final List<OAuth2UserService<R, U>> userServices;
    private final Map<String, OAuth2UserService<R, U>> userServiceMap;

    public SocialDelegatingOAuth2UserService(List<OAuth2UserService<R, U>> userServices) {
        Assert.notEmpty(userServices, (String)"userServices cannot be empty");
        this.userServices = Collections.unmodifiableList(new ArrayList<OAuth2UserService<R, U>>(userServices));
        this.userServiceMap = Collections.emptyMap();
    }

    public SocialDelegatingOAuth2UserService(Map<String, OAuth2UserService<R, U>> userServiceMap) {
        Assert.notEmpty(userServiceMap, (String)"userServiceMap cannot be empty");
        this.userServiceMap = Collections.unmodifiableMap(userServiceMap);
        this.userServices = Collections.emptyList();
    }

    public U loadUser(R userRequest) throws OAuth2AuthenticationException {
        Assert.notNull(userRequest, (String)"userRequest cannot be null");
        if (CollectionUtils.isEmpty(this.userServiceMap)) {
            return (U)((OAuth2User)this.userServices.stream().map(userService -> userService.loadUser(userRequest)).filter(Objects::nonNull).findFirst().orElse(null));
        }
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Object oAuth2UserService = this.userServiceMap.get(registrationId);
        if (oAuth2UserService == null) {
            oAuth2UserService = this.defaultOAuth2UserService;
        }
        return (U)oAuth2UserService.loadUser(userRequest);
    }
}

