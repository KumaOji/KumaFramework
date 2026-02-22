/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  org.springframework.security.authentication.AbstractAuthenticationToken
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.web.context.request.ServletWebRequest
 */
package com.kuma.boot.security.spring.authentication.login.extension.oneClick;

import java.util.Collection;
import java.util.Map;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.context.request.ServletWebRequest;

public class OneClickLoginAuthenticationToken
extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 620L;
    private final Object principal;
    private final Map<String, String> otherParamMap;
    private ServletWebRequest request;

    public OneClickLoginAuthenticationToken(@NonNull String mobile, @Nullable Map<String, String> otherParamMap) {
        this(mobile, otherParamMap, (ServletWebRequest)null);
    }

    public OneClickLoginAuthenticationToken(@NonNull String mobile, @Nullable Map<String, String> otherParamMap, @Nullable ServletWebRequest request) {
        super(null);
        this.principal = mobile;
        this.otherParamMap = otherParamMap;
        this.request = request;
        this.setAuthenticated(false);
    }

    public OneClickLoginAuthenticationToken(@NonNull Object principal, @Nullable Map<String, String> otherParamMap, @NonNull Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.otherParamMap = otherParamMap;
        this.request = null;
        super.setAuthenticated(true);
    }

    public Object getCredentials() {
        return null;
    }

    public Object getPrincipal() {
        return this.principal;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    public void eraseCredentials() {
        super.eraseCredentials();
    }

    @NonNull
    public ServletWebRequest getRequest() {
        return this.request;
    }

    @Nullable
    public Map<String, String> getOtherParamMap() {
        return this.otherParamMap;
    }

    public static OneClickLoginAuthenticationToken unauthenticated(String mobile, Map<String, String> otherParamMap) {
        return new OneClickLoginAuthenticationToken(mobile, otherParamMap);
    }

    public static OneClickLoginAuthenticationToken unauthenticated(String mobile, Map<String, String> otherParamMap, ServletWebRequest request) {
        return new OneClickLoginAuthenticationToken(mobile, otherParamMap, request);
    }

    public static OneClickLoginAuthenticationToken authenticated(@NonNull Object principal, @Nullable Map<String, String> otherParamMap, @NonNull Collection<? extends GrantedAuthority> authorities) {
        return new OneClickLoginAuthenticationToken(principal, otherParamMap, authorities);
    }
}

