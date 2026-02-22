/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.support.MessageSourceAccessor
 *  org.springframework.http.HttpMethod
 *  org.springframework.security.authentication.AuthenticationProvider
 *  org.springframework.security.config.annotation.web.HttpSecurityBuilder
 *  org.springframework.security.core.userdetails.UserDetailsChecker
 *  org.springframework.security.crypto.password.PasswordEncoder
 *  org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
 *  org.springframework.security.web.util.matcher.RequestMatcher
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.extension.captcha;

import com.kuma.boot.security.spring.authentication.login.extension.AbstractExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.ExtensionLoginFilterSecurityConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.captcha.service.CaptchaCheckService;
import com.kuma.boot.security.spring.authentication.login.extension.captcha.service.CaptchaUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.extension.captcha.service.DefaultCaptchaCheckService;
import com.kuma.boot.security.spring.authentication.login.extension.captcha.service.DefaultCaptchaUserDetailsService;
import com.kuma.boot.security.spring.oauth2.token.JwtTokenGenerator;
import com.kuma.boot.security.spring.properties.OAuth2AuthenticationProperties;
import com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

public class CaptchaExtensionLoginFilterConfigurer<H extends HttpSecurityBuilder<H>>
extends AbstractExtensionLoginFilterConfigurer<H, CaptchaExtensionLoginFilterConfigurer<H>, CaptchaAuthenticationFilter, ExtensionLoginFilterSecurityConfigurer<H>> {
    private CaptchaUserDetailsService captchaUserDetailsService;
    private CaptchaCheckService captchaCheckService;

    public CaptchaExtensionLoginFilterConfigurer(ExtensionLoginFilterSecurityConfigurer<H> securityConfigurer) {
        super(securityConfigurer, new CaptchaAuthenticationFilter(), "/login/captcha");
    }

    public CaptchaExtensionLoginFilterConfigurer<H> captchaUserDetailsService(CaptchaUserDetailsService captchaUserDetailsService) {
        this.captchaUserDetailsService = captchaUserDetailsService;
        return this;
    }

    public CaptchaExtensionLoginFilterConfigurer<H> captchaCheckService(CaptchaCheckService captchaCheckService) {
        this.captchaCheckService = captchaCheckService;
        return this;
    }

    public CaptchaExtensionLoginFilterConfigurer<H> jwtTokenGenerator(JwtTokenGenerator jwtTokenGenerator) {
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
        CaptchaUserDetailsService captchaUserDetailsService = this.captchaUserDetailsService != null ? this.captchaUserDetailsService : (CaptchaUserDetailsService)OAuth2AuthorizationUtils.getBeanProvider(applicationContext, CaptchaUserDetailsService.class).getIfAvailable(DefaultCaptchaUserDetailsService::new);
        Assert.notNull((Object)captchaUserDetailsService, (String)"captchaUserDetailsService is required");
        CaptchaCheckService captchaCheckService = this.captchaCheckService != null ? this.captchaCheckService : (CaptchaCheckService)OAuth2AuthorizationUtils.getBeanProvider(applicationContext, CaptchaCheckService.class).getIfAvailable(DefaultCaptchaCheckService::new);
        Assert.notNull((Object)captchaCheckService, (String)"captchaService is required");
        CaptchaAuthenticationFilter captchaAuthenticationFilter = (CaptchaAuthenticationFilter)((Object)this.getAuthenticationFilter());
        OAuth2AuthorizationUtils.oAuth2AuthenticationProperties(applicationContext).ifAvailable(properties -> {
            OAuth2AuthenticationProperties.ExtensionLogin.CaptchaLogin captchaLogin = properties.getExtensionLogin().getCaptchaLogin();
            captchaAuthenticationFilter.setUsernameParameter(captchaLogin.getUsernameParameter());
            captchaAuthenticationFilter.setPasswordParameter(captchaLogin.getPasswordParameter());
            captchaAuthenticationFilter.setTypeParameter(captchaLogin.getTypeParameter());
            captchaAuthenticationFilter.setVerificationCodeParameter(captchaLogin.getVerificationCodeParameter());
            captchaAuthenticationFilter.setFilterProcessesUrl(captchaLogin.getLoginUrl());
        });
        CaptchaAuthenticationProvider provider = new CaptchaAuthenticationProvider(captchaUserDetailsService, captchaCheckService);
        OAuth2AuthorizationUtils.getBeanProvider(applicationContext, PasswordEncoder.class).ifAvailable(provider::setPasswordEncoder);
        OAuth2AuthorizationUtils.getBeanProvider(applicationContext, UserDetailsChecker.class).ifAvailable(provider::setPostAuthenticationChecks);
        OAuth2AuthorizationUtils.getBeanProvider(applicationContext, MessageSourceAccessor.class).ifAvailable(provider::setMessages);
        return provider;
    }
}

