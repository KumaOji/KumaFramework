/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.context.ApplicationContext
 *  org.springframework.http.HttpMethod
 *  org.springframework.security.authentication.AuthenticationProvider
 *  org.springframework.security.config.annotation.web.HttpSecurityBuilder
 *  org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
 *  org.springframework.security.web.util.matcher.RequestMatcher
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.extension.email;

import com.kuma.boot.security.spring.authentication.login.extension.AbstractExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.ExtensionLoginFilterSecurityConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.email.service.DefaultEmailCheckService;
import com.kuma.boot.security.spring.authentication.login.extension.email.service.DefaultEmailUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.extension.email.service.EmailCheckService;
import com.kuma.boot.security.spring.authentication.login.extension.email.service.EmailUserDetailsService;
import com.kuma.boot.security.spring.oauth2.token.JwtTokenGenerator;
import com.kuma.boot.security.spring.properties.OAuth2AuthenticationProperties;
import com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

public class EmailExtensionLoginFilterConfigurer<H extends HttpSecurityBuilder<H>>
extends AbstractExtensionLoginFilterConfigurer<H, EmailExtensionLoginFilterConfigurer<H>, EmailAuthenticationFilter, ExtensionLoginFilterSecurityConfigurer<H>> {
    private EmailUserDetailsService emailUserDetailsService;
    private EmailCheckService emailCheckService;

    public EmailExtensionLoginFilterConfigurer(ExtensionLoginFilterSecurityConfigurer<H> securityConfigurer) {
        super(securityConfigurer, new EmailAuthenticationFilter(), "/login/email");
    }

    public EmailExtensionLoginFilterConfigurer<H> emailUserDetailsService(EmailUserDetailsService emailUserDetailsService) {
        this.emailUserDetailsService = emailUserDetailsService;
        return this;
    }

    public EmailExtensionLoginFilterConfigurer<H> emailCheckService(EmailCheckService emailCheckService) {
        this.emailCheckService = emailCheckService;
        return this;
    }

    public EmailExtensionLoginFilterConfigurer<H> jwtTokenGenerator(JwtTokenGenerator jwtTokenGenerator) {
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
        ObjectProvider<EmailUserDetailsService> emailUserDetailsServiceObjectProvider = OAuth2AuthorizationUtils.getBeanProvider(applicationContext, EmailUserDetailsService.class);
        EmailUserDetailsService emailUserDetailsService = this.emailUserDetailsService != null ? this.emailUserDetailsService : (EmailUserDetailsService)emailUserDetailsServiceObjectProvider.getIfAvailable(DefaultEmailUserDetailsService::new);
        Assert.notNull((Object)emailUserDetailsService, (String)"emailUserDetailsService is required");
        ObjectProvider<EmailCheckService> emailCheckServiceObjectProvider = OAuth2AuthorizationUtils.getBeanProvider(applicationContext, EmailCheckService.class);
        EmailCheckService emailCheckService = this.emailCheckService != null ? this.emailCheckService : (EmailCheckService)emailCheckServiceObjectProvider.getIfAvailable(DefaultEmailCheckService::new);
        Assert.notNull((Object)emailCheckService, (String)"emailCheckService is required");
        EmailAuthenticationFilter emailAuthenticationFilter = (EmailAuthenticationFilter)((Object)this.getAuthenticationFilter());
        OAuth2AuthorizationUtils.oAuth2AuthenticationProperties(applicationContext).ifAvailable(properties -> {
            OAuth2AuthenticationProperties.ExtensionLogin.EmailLogin emailLogin = properties.getExtensionLogin().getEmailLogin();
            emailAuthenticationFilter.setEmailParameter(emailLogin.getEmailParameter());
            emailAuthenticationFilter.setEmailCodeParameter(emailLogin.getEmailCodeParameter());
            emailAuthenticationFilter.setFilterProcessesUrl(emailLogin.getLoginUrl());
        });
        return new EmailAuthenticationProvider(emailUserDetailsService, emailCheckService);
    }
}

