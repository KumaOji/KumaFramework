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
package com.kuma.boot.security.spring.authentication.login.extension.gestures;

import com.kuma.boot.security.spring.authentication.login.extension.AbstractExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.ExtensionLoginFilterSecurityConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.gestures.service.DefaultGesturesUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.extension.gestures.service.GesturesUserDetailsService;
import com.kuma.boot.security.spring.oauth2.token.JwtTokenGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

public class GesturesExtensionLoginFilterConfigurer<H extends HttpSecurityBuilder<H>>
extends AbstractExtensionLoginFilterConfigurer<H, GesturesExtensionLoginFilterConfigurer<H>, GesturesAuthenticationFilter, ExtensionLoginFilterSecurityConfigurer<H>> {
    private GesturesUserDetailsService gesturesUserDetailsService;

    public GesturesExtensionLoginFilterConfigurer(ExtensionLoginFilterSecurityConfigurer<H> securityConfigurer) {
        super(securityConfigurer, new GesturesAuthenticationFilter(), "/login/gestures");
    }

    public GesturesExtensionLoginFilterConfigurer<H> gesturesUserDetailsService(GesturesUserDetailsService gesturesUserDetailsService) {
        this.gesturesUserDetailsService = gesturesUserDetailsService;
        return this;
    }

    public GesturesExtensionLoginFilterConfigurer<H> jwtTokenGenerator(JwtTokenGenerator jwtTokenGenerator) {
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
        GesturesUserDetailsService gesturesUserDetailsService = this.gesturesUserDetailsService != null ? this.gesturesUserDetailsService : new DefaultGesturesUserDetailsService();
        Assert.notNull((Object)gesturesUserDetailsService, (String)"gesturesUserDetailsService is required");
        return new GesturesAuthenticationProvider(gesturesUserDetailsService);
    }
}

