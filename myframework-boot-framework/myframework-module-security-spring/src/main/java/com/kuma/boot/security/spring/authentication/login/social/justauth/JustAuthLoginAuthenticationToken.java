/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.security.authentication.AbstractAuthenticationToken
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth;

import com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.Assert;

public class JustAuthLoginAuthenticationToken
extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 620L;
    private final Auth2DefaultRequest auth2DefaultRequest;
    private final HttpServletRequest request;

    public JustAuthLoginAuthenticationToken(Auth2DefaultRequest auth2DefaultRequest, HttpServletRequest request) {
        super(Collections.emptyList());
        Assert.notNull((Object)auth2DefaultRequest, (String)"auth2DefaultRequest cannot be null");
        Assert.notNull((Object)request, (String)"request cannot be null");
        this.auth2DefaultRequest = auth2DefaultRequest;
        this.setAuthenticated(false);
        this.request = request;
    }

    public Object getCredentials() {
        return "";
    }

    public Object getPrincipal() {
        return null;
    }

    public Auth2DefaultRequest getAuth2DefaultRequest() {
        return this.auth2DefaultRequest;
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }
}

