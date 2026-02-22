/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.context.ApplicationContext
 *  org.springframework.security.oauth2.core.AuthenticationMethod
 *  org.springframework.security.oauth2.core.AuthorizationGrantType
 *  org.springframework.security.oauth2.core.ClientAuthenticationMethod
 */
package com.kuma.boot.security.spring.utils;

import com.kuma.boot.security.spring.properties.OAuth2AuthenticationProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

public class OAuth2AuthorizationUtils {
    public static AuthorizationGrantType resolveAuthorizationGrantType(String authorizationGrantType) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        }
        if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.CLIENT_CREDENTIALS;
        }
        if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        }
        if (AuthorizationGrantType.DEVICE_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.DEVICE_CODE;
        }
        if (AuthorizationGrantType.JWT_BEARER.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.JWT_BEARER;
        }
        return new AuthorizationGrantType(authorizationGrantType);
    }

    public static ClientAuthenticationMethod resolveClientAuthenticationMethod(String clientAuthenticationMethod) {
        if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        }
        if (ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_POST;
        }
        if (ClientAuthenticationMethod.CLIENT_SECRET_JWT.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_JWT;
        }
        if (ClientAuthenticationMethod.PRIVATE_KEY_JWT.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.PRIVATE_KEY_JWT;
        }
        if (ClientAuthenticationMethod.NONE.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.NONE;
        }
        return new ClientAuthenticationMethod(clientAuthenticationMethod);
    }

    public static AuthenticationMethod resolveAuthenticationMethod(String authenticationMethod) {
        if (AuthenticationMethod.HEADER.getValue().equals(authenticationMethod)) {
            return AuthenticationMethod.HEADER;
        }
        if (AuthenticationMethod.FORM.getValue().equals(authenticationMethod)) {
            return AuthenticationMethod.FORM;
        }
        if (AuthenticationMethod.QUERY.getValue().equals(authenticationMethod)) {
            return AuthenticationMethod.QUERY;
        }
        return new AuthenticationMethod(authenticationMethod);
    }

    public static <T> ObjectProvider<T> getBeanProvider(ApplicationContext applicationContext, Class<T> requiredType) {
        return applicationContext.getBeanProvider(requiredType);
    }

    public static ObjectProvider<OAuth2AuthenticationProperties> oAuth2AuthenticationProperties(ApplicationContext applicationContext) {
        return applicationContext.getBeanProvider(OAuth2AuthenticationProperties.class);
    }
}

