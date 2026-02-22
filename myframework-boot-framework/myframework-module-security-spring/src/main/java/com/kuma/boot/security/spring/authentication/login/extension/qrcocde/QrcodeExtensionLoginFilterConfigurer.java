/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.context.ApplicationContext
 *  org.springframework.http.HttpMethod
 *  org.springframework.security.authentication.AuthenticationProvider
 *  org.springframework.security.config.annotation.web.HttpSecurityBuilder
 *  org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
 *  org.springframework.security.web.util.matcher.RequestMatcher
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.extension.qrcocde;

import com.kuma.boot.security.spring.authentication.login.extension.AbstractExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.ExtensionLoginFilterSecurityConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.qrcocde.service.DefaultQrcodeService;
import com.kuma.boot.security.spring.authentication.login.extension.qrcocde.service.DefaultQrcodeUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.extension.qrcocde.service.QrcodeService;
import com.kuma.boot.security.spring.authentication.login.extension.qrcocde.service.QrcodeUserDetailsService;
import com.kuma.boot.security.spring.oauth2.token.JwtTokenGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

public class QrcodeExtensionLoginFilterConfigurer<H extends HttpSecurityBuilder<H>>
extends AbstractExtensionLoginFilterConfigurer<H, QrcodeExtensionLoginFilterConfigurer<H>, QrcodeAuthenticationFilter, ExtensionLoginFilterSecurityConfigurer<H>> {
    private QrcodeUserDetailsService qrcodeUserDetailsService;
    private QrcodeService qrcodeService;

    public QrcodeExtensionLoginFilterConfigurer(ExtensionLoginFilterSecurityConfigurer<H> securityConfigurer) {
        super(securityConfigurer, new QrcodeAuthenticationFilter(), "/login/qrcode");
    }

    public QrcodeExtensionLoginFilterConfigurer<H> accountUserDetailsService(QrcodeUserDetailsService qrcodeUserDetailsService) {
        this.qrcodeUserDetailsService = qrcodeUserDetailsService;
        return this;
    }

    public QrcodeExtensionLoginFilterConfigurer<H> qrcodeService(QrcodeService qrcodeService) {
        this.qrcodeService = qrcodeService;
        return this;
    }

    public QrcodeExtensionLoginFilterConfigurer<H> jwtTokenGenerator(JwtTokenGenerator jwtTokenGenerator) {
        this.setJwtTokenGenerator(jwtTokenGenerator);
        return this;
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, loginProcessingUrl);
    }

    @Override
    protected AuthenticationProvider authenticationProvider(H http) {
        ApplicationContext applicationContext = (ApplicationContext)http.getSharedObject(ApplicationContext.class);
        QrcodeUserDetailsService qrcodeUserDetailsService = this.qrcodeUserDetailsService != null ? this.qrcodeUserDetailsService : new DefaultQrcodeUserDetailsService();
        Assert.notNull((Object)qrcodeUserDetailsService, (String)"qrcodeUserDetailsService is required");
        QrcodeService qrcodeService = this.qrcodeService != null ? this.qrcodeService : new DefaultQrcodeService();
        Assert.notNull((Object)qrcodeService, (String)"captchaUserDetailsService is required");
        return new QrcodeAuthenticationProvider(qrcodeService, qrcodeUserDetailsService);
    }
}

