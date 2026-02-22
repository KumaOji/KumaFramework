/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.security.authentication.AuthenticationDetailsSource
 */
package com.kuma.boot.security.spring.authentication.login.form;

import com.kuma.boot.security.spring.properties.OAuth2AuthenticationProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationDetailsSource;

public class FormLoginWebAuthenticationDetailSource
implements AuthenticationDetailsSource<HttpServletRequest, FormLoginWebAuthenticationDetails> {
    private final OAuth2AuthenticationProperties authenticationProperties;

    public FormLoginWebAuthenticationDetailSource(OAuth2AuthenticationProperties authenticationProperties) {
        this.authenticationProperties = authenticationProperties;
    }

    public FormLoginWebAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new FormLoginWebAuthenticationDetails(context, this.authenticationProperties.getFormLogin().getCloseCaptcha(), this.authenticationProperties.getFormLogin().getCaptchaParameter(), this.authenticationProperties.getFormLogin().getCategory());
    }
}

