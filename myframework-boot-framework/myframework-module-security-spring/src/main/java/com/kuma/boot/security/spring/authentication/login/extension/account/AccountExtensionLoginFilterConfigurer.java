/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.context.ApplicationContext
 *  org.springframework.http.HttpMethod
 *  org.springframework.security.authentication.AuthenticationProvider
 *  org.springframework.security.config.annotation.web.HttpSecurityBuilder
 *  org.springframework.security.crypto.password.PasswordEncoder
 *  org.springframework.security.web.context.DelegatingSecurityContextRepository
 *  org.springframework.security.web.context.HttpSessionSecurityContextRepository
 *  org.springframework.security.web.context.RequestAttributeSecurityContextRepository
 *  org.springframework.security.web.context.SecurityContextRepository
 *  org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
 *  org.springframework.security.web.util.matcher.RequestMatcher
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.extension.account;

import com.kuma.boot.security.spring.authentication.login.extension.AbstractExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.ExtensionLoginFilterSecurityConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.account.service.AccountUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.extension.account.service.DefaultAccountUserDetailsService;
import com.kuma.boot.security.spring.oauth2.token.JwtTokenGenerator;
import com.kuma.boot.security.spring.properties.OAuth2AuthenticationProperties;
import com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

public class AccountExtensionLoginFilterConfigurer<H extends HttpSecurityBuilder<H>>
extends AbstractExtensionLoginFilterConfigurer<H, AccountExtensionLoginFilterConfigurer<H>, AccountAuthenticationFilter, ExtensionLoginFilterSecurityConfigurer<H>> {
    private AccountUserDetailsService accountUserDetailsService;

    public AccountExtensionLoginFilterConfigurer(ExtensionLoginFilterSecurityConfigurer<H> securityConfigurer) {
        super(securityConfigurer, new AccountAuthenticationFilter(), "/login/account");
    }

    public AccountExtensionLoginFilterConfigurer<H> accountUserDetailsService(AccountUserDetailsService accountUserDetailsService) {
        this.accountUserDetailsService = accountUserDetailsService;
        return this;
    }

    public AccountExtensionLoginFilterConfigurer<H> jwtTokenGenerator(JwtTokenGenerator jwtTokenGenerator) {
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
        AccountUserDetailsService accountUserDetailsService = this.accountUserDetailsService != null ? this.accountUserDetailsService : (AccountUserDetailsService)OAuth2AuthorizationUtils.getBeanProvider(applicationContext, AccountUserDetailsService.class).getIfAvailable(DefaultAccountUserDetailsService::new);
        Assert.notNull((Object)accountUserDetailsService, (String)"accountUserDetailsService is required");
        AccountAuthenticationFilter authenticationFilter = (AccountAuthenticationFilter)((Object)this.getAuthenticationFilter());
        DelegatingSecurityContextRepository securityContextRepository = new DelegatingSecurityContextRepository(new SecurityContextRepository[]{new RequestAttributeSecurityContextRepository(), new HttpSessionSecurityContextRepository()});
        authenticationFilter.setSecurityContextRepository((SecurityContextRepository)securityContextRepository);
        OAuth2AuthorizationUtils.oAuth2AuthenticationProperties(applicationContext).ifAvailable(properties -> {
            OAuth2AuthenticationProperties.ExtensionLogin.AccountLogin accountLogin = properties.getExtensionLogin().getAccountLogin();
            authenticationFilter.setUsernameParameter(accountLogin.getUsernameParameter());
            authenticationFilter.setPasswordParameter(accountLogin.getPasswordParameter());
            authenticationFilter.setTypeParameter(accountLogin.getTypeParameter());
            authenticationFilter.setFilterProcessesUrl(accountLogin.getLoginUrl());
        });
        AccountAuthenticationProvider provider = new AccountAuthenticationProvider(accountUserDetailsService);
        OAuth2AuthorizationUtils.getBeanProvider(applicationContext, PasswordEncoder.class).ifAvailable(provider::setPasswordEncoder);
        return provider;
    }
}

