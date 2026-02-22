/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.authentication.AbstractAuthenticationToken
 */
package com.kuma.boot.security.spring.authentication.login.extension.mfa.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class MfaAuthenticationToken
extends AbstractAuthenticationToken {
    private final Object principal;
    private final String credentials;
    private boolean mfa;

    public MfaAuthenticationToken(Object principal, String credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.setAuthenticated(false);
    }

    public MfaAuthenticationToken(Object principal, String credentials, boolean mfa) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.mfa = mfa;
    }

    public String getCredentials() {
        return this.credentials;
    }

    public Object getPrincipal() {
        return this.principal;
    }

    public boolean isMfa() {
        return this.mfa;
    }
}

