/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest
 *  jakarta.servlet.http.HttpServletRequest
 *  me.zhyd.oauth.model.AuthCallback
 *  me.zhyd.oauth.model.AuthResponse
 *  me.zhyd.oauth.model.AuthUser
 *  org.springframework.security.oauth2.core.OAuth2AuthenticationException
 *  org.springframework.security.oauth2.core.OAuth2Error
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.service;

import com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest;
import jakarta.servlet.http.HttpServletRequest;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.util.Assert;

public class DefaultAuth2UserService
implements Auth2UserService {
    @Override
    public AuthUser loadUser(Auth2DefaultRequest auth2Request, HttpServletRequest request) throws OAuth2AuthenticationException {
        Assert.notNull((Object)auth2Request, (String)"auth2Request cannot be null");
        AuthCallback authCallback = AuthCallback.builder().code(request.getParameter("code")).state(request.getParameter("state")).auth_code(request.getParameter("auth_code")).authorization_code(request.getParameter("authorization_code")).oauth_token(request.getParameter("oauth_token")).oauth_verifier(request.getParameter("oauth_verifier")).build();
        AuthResponse authResponse = auth2Request.login(authCallback);
        if (authResponse.ok()) {
            AuthUser authUser = (AuthUser)authResponse.getData();
            authUser.setSource(auth2Request.getProviderId());
            return authUser;
        }
        String msg = authResponse.getMsg();
        OAuth2Error oauth2Error = new OAuth2Error(msg, String.format(" for Client Registration: %s", auth2Request.getProviderId()), request.getRequestURI());
        throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
    }
}

