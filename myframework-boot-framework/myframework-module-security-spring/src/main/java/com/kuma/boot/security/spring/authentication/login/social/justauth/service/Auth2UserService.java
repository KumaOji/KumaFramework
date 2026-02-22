/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest
 *  jakarta.servlet.http.HttpServletRequest
 *  me.zhyd.oauth.model.AuthUser
 *  org.springframework.security.oauth2.core.OAuth2AuthenticationException
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.service;

import com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest;
import jakarta.servlet.http.HttpServletRequest;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

public interface Auth2UserService {
    public AuthUser loadUser(Auth2DefaultRequest var1, HttpServletRequest var2) throws OAuth2AuthenticationException;
}

