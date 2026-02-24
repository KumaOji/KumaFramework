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

package com.kuma.boot.security.spring.utils;

import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2AuthenticationProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

/**
 * <p>OAuth2 存储通用工具类
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:08:25
 */
public class OAuth2AuthorizationUtils {

    /**
     * 解决授权授予类型
     *
     * @param authorizationGrantType 授权批准类型
     * @return {@link AuthorizationGrantType }
     * @since 2023-07-04 10:08:25
     */
    public static AuthorizationGrantType resolveAuthorizationGrantType(
            String authorizationGrantType) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        } else if (AuthorizationGrantType.CLIENT_CREDENTIALS
                .getValue()
                .equals(authorizationGrantType)) {
            return AuthorizationGrantType.CLIENT_CREDENTIALS;
        } else if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        } else if (AuthorizationGrantType.DEVICE_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.DEVICE_CODE;
        } else if (AuthorizationGrantType.JWT_BEARER.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.JWT_BEARER;
        }
        // Custom authorization grant type
        return new AuthorizationGrantType(authorizationGrantType);
    }

    /**
     * 解决客户端身份验证方法
     *
     * @param clientAuthenticationMethod 客户端身份验证方法
     * @return {@link ClientAuthenticationMethod }
     * @since 2023-07-04 10:08:25
     */
    public static ClientAuthenticationMethod resolveClientAuthenticationMethod(
            String clientAuthenticationMethod) {
        if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC
                .getValue()
                .equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        } else if (ClientAuthenticationMethod.CLIENT_SECRET_POST
                .getValue()
                .equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_POST;
        } else if (ClientAuthenticationMethod.CLIENT_SECRET_JWT
                .getValue()
                .equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_JWT;
        } else if (ClientAuthenticationMethod.PRIVATE_KEY_JWT
                .getValue()
                .equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.PRIVATE_KEY_JWT;
        } else if (ClientAuthenticationMethod.NONE.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.NONE;
        }
        return new ClientAuthenticationMethod(clientAuthenticationMethod);
    }

    /**
     * 解决身份验证方法
     *
     * @param authenticationMethod 身份验证方法
     * @return {@link AuthenticationMethod }
     * @since 2023-07-04 10:08:25
     */
    public static AuthenticationMethod resolveAuthenticationMethod(String authenticationMethod) {
        if (AuthenticationMethod.HEADER.getValue().equals(authenticationMethod)) {
            return AuthenticationMethod.HEADER;
        } else if (AuthenticationMethod.FORM.getValue().equals(authenticationMethod)) {
            return AuthenticationMethod.FORM;
        } else if (AuthenticationMethod.QUERY.getValue().equals(authenticationMethod)) {
            return AuthenticationMethod.QUERY;
        }
        return new AuthenticationMethod(authenticationMethod);
    }

    public static <T> ObjectProvider<T> getBeanProvider(
            ApplicationContext applicationContext, Class<T> requiredType) {
        return applicationContext.getBeanProvider(requiredType);
    }

    public static ObjectProvider<OAuth2AuthenticationProperties> oAuth2AuthenticationProperties(
            ApplicationContext applicationContext) {
        return applicationContext.getBeanProvider(OAuth2AuthenticationProperties.class);
    }
}
