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
package com.kuma.boot.security.spring.authentication.login.extension.wechatmp;

import com.kuma.boot.security.spring.authentication.login.extension.AbstractExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.ExtensionLoginFilterSecurityConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.wechatmp.service.DefaultWechatWechatMpUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.extension.wechatmp.service.WechatMpUserDetailsService;
import com.kuma.boot.security.spring.oauth2.token.JwtTokenGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

public class WechatMpExtensionLoginFilterConfigurer<H extends HttpSecurityBuilder<H>>
extends AbstractExtensionLoginFilterConfigurer<H, WechatMpExtensionLoginFilterConfigurer<H>, WechatMpAuthenticationFilter, ExtensionLoginFilterSecurityConfigurer<H>> {
    private WechatMpUserDetailsService wechatMpUserDetailsService;

    public WechatMpExtensionLoginFilterConfigurer(ExtensionLoginFilterSecurityConfigurer<H> securityConfigurer) {
        super(securityConfigurer, new WechatMpAuthenticationFilter(), "/login/mp");
    }

    public WechatMpExtensionLoginFilterConfigurer<H> mpUserDetailsService(WechatMpUserDetailsService wechatMpUserDetailsService) {
        this.wechatMpUserDetailsService = wechatMpUserDetailsService;
        return this;
    }

    public WechatMpExtensionLoginFilterConfigurer<H> jwtTokenGenerator(JwtTokenGenerator jwtTokenGenerator) {
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
        WechatMpUserDetailsService wechatMpUserDetailsService = this.wechatMpUserDetailsService != null ? this.wechatMpUserDetailsService : new DefaultWechatWechatMpUserDetailsService();
        Assert.notNull((Object)wechatMpUserDetailsService, (String)"mpUserDetailsService is required");
        return new WechatMpAuthenticationProvider(wechatMpUserDetailsService);
    }
}

