/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.filter.login;

import java.util.Map;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

final class Auth2AuthorizationResponseUtils {
    private Auth2AuthorizationResponseUtils() {
    }

    static MultiValueMap<String, String> toMultiMap(Map<String, String[]> map) {
        LinkedMultiValueMap params = new LinkedMultiValueMap(map.size());
        map.forEach((arg_0, arg_1) -> Auth2AuthorizationResponseUtils.lambda$toMultiMap$0((MultiValueMap)params, arg_0, arg_1));
        return params;
    }

    static boolean isAuthorizationResponse(MultiValueMap<String, String> request) {
        return Auth2AuthorizationResponseUtils.isAuthorizationResponseSuccess(request) || Auth2AuthorizationResponseUtils.isAuthorizationResponseError(request);
    }

    static boolean isAuthorizationResponseSuccess(MultiValueMap<String, String> request) {
        return StringUtils.hasText((String)((String)request.getFirst((Object)"code"))) && StringUtils.hasText((String)((String)request.getFirst((Object)"state")));
    }

    static boolean isAuthorizationResponseError(MultiValueMap<String, String> request) {
        return StringUtils.hasText((String)((String)request.getFirst((Object)"error"))) && StringUtils.hasText((String)((String)request.getFirst((Object)"state")));
    }

    static OAuth2AuthorizationResponse convert(MultiValueMap<String, String> request, String redirectUri) {
        String code = (String)request.getFirst((Object)"code");
        String errorCode = (String)request.getFirst((Object)"error");
        String state = (String)request.getFirst((Object)"state");
        if (StringUtils.hasText((String)code)) {
            return OAuth2AuthorizationResponse.success((String)code).redirectUri(redirectUri).state(state).build();
        }
        String errorDescription = (String)request.getFirst((Object)"error_description");
        String errorUri = (String)request.getFirst((Object)"error_uri");
        return OAuth2AuthorizationResponse.error((String)errorCode).redirectUri(redirectUri).errorDescription(errorDescription).errorUri(errorUri).state(state).build();
    }

    private static /* synthetic */ void lambda$toMultiMap$0(MultiValueMap params, String key, String[] values) {
        for (String value : values) {
            params.add((Object)key, (Object)value);
        }
    }
}

