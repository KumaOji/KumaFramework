/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest
 *  jakarta.servlet.http.HttpServletRequest
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.filter.redirect;

import com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface Auth2AuthorizationRequestResolver {
    public Auth2DefaultRequest resolve(HttpServletRequest var1);

    public Auth2DefaultRequest resolve(HttpServletRequest var1, String var2);
}

