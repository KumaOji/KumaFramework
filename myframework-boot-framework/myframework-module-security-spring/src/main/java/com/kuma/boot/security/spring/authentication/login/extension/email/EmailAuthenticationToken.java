/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.authentication.AbstractAuthenticationToken
 *  org.springframework.security.core.GrantedAuthority
 */
package com.kuma.boot.security.spring.authentication.login.extension.email;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class EmailAuthenticationToken
extends AbstractAuthenticationToken {
    private final Object principal;
    private String emailCode;

    public EmailAuthenticationToken(Object principal, String emailCode) {
        super(null);
        this.emailCode = emailCode;
        this.principal = principal;
        this.setAuthenticated(false);
    }

    public EmailAuthenticationToken(Object principal, String emailCode, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.emailCode = emailCode;
        this.principal = principal;
        super.setAuthenticated(true);
    }

    public static EmailAuthenticationToken unauthenticated(Object principal, String emailCode) {
        return new EmailAuthenticationToken(principal, emailCode);
    }

    public static EmailAuthenticationToken authenticated(Object principal, String emailCode, Collection<? extends GrantedAuthority> authorities) {
        return new EmailAuthenticationToken(principal, emailCode, authorities);
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

    public String getEmailCode() {
        return this.emailCode;
    }

    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }
}

