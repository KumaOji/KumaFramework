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
package com.kuma.boot.security.spring.authentication.login.extension.sms;

import com.kuma.boot.security.spring.authentication.login.extension.AbstractExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.ExtensionLoginFilterSecurityConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.sms.service.DefaultSmsCheckCodeService;
import com.kuma.boot.security.spring.authentication.login.extension.sms.service.DefaultSmsUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.extension.sms.service.SmsCheckCodeService;
import com.kuma.boot.security.spring.authentication.login.extension.sms.service.SmsUserDetailsService;
import com.kuma.boot.security.spring.oauth2.token.JwtTokenGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

public class SmsExtensionLoginFilterConfigurer<H extends HttpSecurityBuilder<H>>
extends AbstractExtensionLoginFilterConfigurer<H, SmsExtensionLoginFilterConfigurer<H>, SmsAuthenticationFilter, ExtensionLoginFilterSecurityConfigurer<H>> {
    private SmsUserDetailsService smsUserDetailsService;
    private SmsCheckCodeService smsCheckCodeService;

    public SmsExtensionLoginFilterConfigurer(ExtensionLoginFilterSecurityConfigurer<H> securityConfigurer) {
        super(securityConfigurer, new SmsAuthenticationFilter(), "/login/phone");
    }

    public SmsExtensionLoginFilterConfigurer<H> smsUserDetailsService(SmsUserDetailsService smsUserDetailsService) {
        this.smsUserDetailsService = smsUserDetailsService;
        return this;
    }

    public SmsExtensionLoginFilterConfigurer<H> smsCheckCodeService(SmsCheckCodeService smsCheckCodeService) {
        this.smsCheckCodeService = smsCheckCodeService;
        return this;
    }

    public SmsExtensionLoginFilterConfigurer<H> jwtTokenGenerator(JwtTokenGenerator jwtTokenGenerator) {
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
        SmsUserDetailsService smsUserDetailsService = this.smsUserDetailsService != null ? this.smsUserDetailsService : new DefaultSmsUserDetailsService();
        Assert.notNull((Object)smsUserDetailsService, (String)"phoneUserDetailsService is required");
        SmsCheckCodeService smsCheckCodeService = this.smsCheckCodeService != null ? this.smsCheckCodeService : new DefaultSmsCheckCodeService();
        Assert.notNull((Object)smsCheckCodeService, (String)"phoneService is required");
        return new SmsAuthenticationProvider(smsUserDetailsService, smsCheckCodeService);
    }
}

