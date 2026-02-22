/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.authentication.AbstractAuthenticationToken
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.extension.account;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

public class AccountAuthenticationToken
extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 620L;
    private final Object principal;
    private String password;
    private String type;

    public AccountAuthenticationToken(Object principal, String password, String type) {
        super(null);
        this.principal = principal;
        this.password = password;
        this.type = type;
        this.setAuthenticated(false);
    }

    public AccountAuthenticationToken(Object principal, String password, String type, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.password = password;
        this.type = type;
        super.setAuthenticated(true);
    }

    public static AccountAuthenticationToken unauthenticated(Object principal, String password, String type) {
        return new AccountAuthenticationToken(principal, password, type);
    }

    public static AccountAuthenticationToken authenticated(Object principal, String password, String type, Collection<? extends GrantedAuthority> authorities) {
        return new AccountAuthenticationToken(principal, password, type, authorities);
    }

    public Object getCredentials() {
        return this.password;
    }

    public Object getPrincipal() {
        return this.principal;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue((!isAuthenticated ? 1 : 0) != 0, (String)"Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    public void eraseCredentials() {
        super.eraseCredentials();
        this.password = null;
    }

    public String getType() {
        return this.type;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setType(String type) {
        this.type = type;
    }
}

