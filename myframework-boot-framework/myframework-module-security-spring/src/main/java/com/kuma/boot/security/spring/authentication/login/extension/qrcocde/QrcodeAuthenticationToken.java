/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.authentication.AbstractAuthenticationToken
 *  org.springframework.security.core.GrantedAuthority
 */
package com.kuma.boot.security.spring.authentication.login.extension.qrcocde;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class QrcodeAuthenticationToken
extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 620L;
    private final Object principal;
    private String password;

    public QrcodeAuthenticationToken(Object principal, String password) {
        super(null);
        this.principal = principal;
        this.password = password;
        this.setAuthenticated(false);
    }

    public QrcodeAuthenticationToken(Object principal, String password, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.password = password;
        super.setAuthenticated(true);
    }

    public Object getCredentials() {
        return this.password;
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
        this.password = null;
    }
}

