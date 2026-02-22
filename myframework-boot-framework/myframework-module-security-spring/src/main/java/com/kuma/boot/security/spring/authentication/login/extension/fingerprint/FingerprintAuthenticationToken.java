/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.authentication.AbstractAuthenticationToken
 *  org.springframework.security.core.GrantedAuthority
 */
package com.kuma.boot.security.spring.authentication.login.extension.fingerprint;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class FingerprintAuthenticationToken
extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 620L;
    private final Object principal;
    private String fingerPrint;

    public FingerprintAuthenticationToken(Object principal, String fingerPrint) {
        super(null);
        this.principal = principal;
        this.fingerPrint = fingerPrint;
        this.setAuthenticated(false);
    }

    public FingerprintAuthenticationToken(Object principal, String fingerPrint, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.fingerPrint = fingerPrint;
        super.setAuthenticated(true);
    }

    public static FingerprintAuthenticationToken unauthenticated(Object principal, String fingerPrint) {
        return new FingerprintAuthenticationToken(principal, fingerPrint);
    }

    public static FingerprintAuthenticationToken authenticated(Object principal, String fingerPrint, Collection<? extends GrantedAuthority> authorities) {
        return new FingerprintAuthenticationToken(principal, fingerPrint, authorities);
    }

    public Object getCredentials() {
        return this.fingerPrint;
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
        this.fingerPrint = null;
    }

    public String getFingerPrint() {
        return this.fingerPrint;
    }

    public void setFingerPrint(String fingerPrint) {
        this.fingerPrint = fingerPrint;
    }
}

