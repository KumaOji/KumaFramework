/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.security.spring.authentication.login.social.oauth2client.github;

import java.util.Map;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import tools.jackson.databind.json.JsonMapper;

public class GithubOAuth2UserService extends DefaultOAuth2UserService {
    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest)
            throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration = oAuth2UserRequest.getClientRegistration();
        String registrationId = clientRegistration.getRegistrationId();

        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String userNameAttributeName =
                clientRegistration
                        .getProviderDetails()
                        .getUserInfoEndpoint()
                        .getUserNameAttributeName();

        com.kuma.boot.security.spring.authentication.login.social.oauth2client.github.GithubOAuth2User githubOAuth2User =
                jsonMapper.convertValue(attributes, com.kuma.boot.security.spring.authentication.login.social.oauth2client.github.GithubOAuth2User.class);
        githubOAuth2User.setNameAttributeKey(userNameAttributeName);
        return githubOAuth2User;
    }
}
