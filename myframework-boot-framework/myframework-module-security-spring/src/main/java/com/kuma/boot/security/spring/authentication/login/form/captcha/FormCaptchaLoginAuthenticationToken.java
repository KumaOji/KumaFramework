/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.authentication.UsernamePasswordAuthenticationToken
 *  org.springframework.security.core.GrantedAuthority
 */
package com.kuma.boot.security.spring.authentication.login.form.captcha;

import java.util.Collection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class FormCaptchaLoginAuthenticationToken
extends UsernamePasswordAuthenticationToken {
    public FormCaptchaLoginAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public FormCaptchaLoginAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}

