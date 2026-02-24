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

package com.kuma.boot.security.spring.authentication.login.social.oauth2client;

import java.util.Arrays;
import java.util.Objects;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

/**
 * customizer {@link OAuth2AuthorizationRequest}
 *
 * <p>client_id 变成 appid ，并追加锚点#wechat_redirect
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-10 17:41:10
 */
public class SocialOAuth2AuthorizationRequestCustomizer {

    /**
     * 授权请求参数定制
     *
     * @param builder the builder
     * @since 2023-07-10 17:41:11
     */
    public static void customize(OAuth2AuthorizationRequest.Builder builder) {
        builder.attributes(
                attributes ->
                        Arrays.stream(com.kuma.boot.security.spring.authentication.login.social.oauth2client.SocialClientProviders.values())
                                .filter(
                                        clientProvider ->
                                                Objects.equals(
                                                        clientProvider.registrationId(),
                                                        attributes.get(
                                                                OAuth2ParameterNames
                                                                        .REGISTRATION_ID)))
                                .findAny()
                                .map(com.kuma.boot.security.spring.authentication.login.social.oauth2client.SocialClientProviders::requestConsumer)
                                .ifPresent(requestConsumer -> requestConsumer.accept(builder)));
    }
}
