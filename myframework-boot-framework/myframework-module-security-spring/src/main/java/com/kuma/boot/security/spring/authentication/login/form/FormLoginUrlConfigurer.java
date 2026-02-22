/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.security.config.annotation.web.builders.HttpSecurity
 *  org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
 */
package com.kuma.boot.security.spring.authentication.login.form;

import com.kuma.boot.security.spring.properties.OAuth2AuthenticationProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;

public class FormLoginUrlConfigurer {
    private final OAuth2AuthenticationProperties authenticationProperties;

    public FormLoginUrlConfigurer(OAuth2AuthenticationProperties authenticationProperties) {
        this.authenticationProperties = authenticationProperties;
    }

    public FormLoginConfigurer<HttpSecurity> from(FormLoginConfigurer<HttpSecurity> configurer) {
        configurer.loginPage(this.getFormLogin().getLoginPageUrl()).usernameParameter(this.getFormLogin().getUsernameParameter()).passwordParameter(this.getFormLogin().getPasswordParameter());
        if (StringUtils.isNotBlank((CharSequence)this.getFormLogin().getFailureForwardUrl())) {
            configurer.failureForwardUrl(this.getFormLogin().getFailureForwardUrl());
        }
        if (StringUtils.isNotBlank((CharSequence)this.getFormLogin().getSuccessForwardUrl())) {
            configurer.successForwardUrl(this.getFormLogin().getSuccessForwardUrl());
        }
        return configurer;
    }

    private OAuth2AuthenticationProperties.FormLogin getFormLogin() {
        return this.authenticationProperties.getFormLogin();
    }
}

