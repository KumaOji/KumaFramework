/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.authentication.AbstractAuthenticationToken
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.extension.captcha;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

public class CaptchaAuthenticationToken
extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 620L;
    private final Object principal;
    private String password;
    private String verificationCode;
    private String type;

    public CaptchaAuthenticationToken(Object principal, String password, String verificationCode, String type) {
        super(null);
        this.principal = principal;
        this.password = password;
        this.verificationCode = verificationCode;
        this.type = type;
        this.setAuthenticated(false);
    }

    public CaptchaAuthenticationToken(Object principal, String password, String verificationCode, String type, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.password = password;
        this.verificationCode = verificationCode;
        this.type = type;
        super.setAuthenticated(true);
    }

    public static CaptchaAuthenticationToken unauthenticated(Object principal, String password, String verificationCode, String type) {
        return new CaptchaAuthenticationToken(principal, password, verificationCode, type);
    }

    public static CaptchaAuthenticationToken authenticated(Object principal, String password, String verificationCode, String type, Collection<? extends GrantedAuthority> authorities) {
        return new CaptchaAuthenticationToken(principal, password, verificationCode, type, authorities);
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

    public String getVerificationCode() {
        return this.verificationCode;
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

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public void setType(String type) {
        this.type = type;
    }
}

