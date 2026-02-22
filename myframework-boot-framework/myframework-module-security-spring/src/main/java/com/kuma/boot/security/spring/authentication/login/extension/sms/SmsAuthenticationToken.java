/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.authentication.AbstractAuthenticationToken
 *  org.springframework.security.core.GrantedAuthority
 */
package com.kuma.boot.security.spring.authentication.login.extension.sms;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class SmsAuthenticationToken
extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 620L;
    private final Object principal;
    private String captcha;
    private String type;

    public SmsAuthenticationToken(Object principal, String captcha, String type) {
        super(null);
        this.principal = principal;
        this.captcha = captcha;
        this.type = type;
        this.setAuthenticated(false);
    }

    public SmsAuthenticationToken(Object principal, String captcha, String type, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.captcha = captcha;
        this.type = type;
        super.setAuthenticated(true);
    }

    public Object getCredentials() {
        return this.captcha;
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

    public static SmsAuthenticationToken unauthenticated(Object principal, String captcha, String type) {
        return new SmsAuthenticationToken(principal, captcha, type);
    }

    public static SmsAuthenticationToken authenticated(Object principal, String captcha, String type, Collection<? extends GrantedAuthority> authorities) {
        return new SmsAuthenticationToken(principal, captcha, type, authorities);
    }

    public void eraseCredentials() {
        super.eraseCredentials();
        this.captcha = null;
    }

    public String getType() {
        return this.type;
    }
}

