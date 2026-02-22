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
package com.kuma.boot.security.spring.authentication.login.extension.oneClick;

import com.kuma.boot.security.spring.authentication.login.extension.AbstractExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.ExtensionLoginFilterSecurityConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.oneClick.service.DefaultOneClickLoginService;
import com.kuma.boot.security.spring.authentication.login.extension.oneClick.service.DefaultOneClickUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.extension.oneClick.service.OneClickLoginService;
import com.kuma.boot.security.spring.authentication.login.extension.oneClick.service.OneClickUserDetailsService;
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

public class OneClickExtensionLoginFilterConfigurer<H extends HttpSecurityBuilder<H>>
extends AbstractExtensionLoginFilterConfigurer<H, OneClickExtensionLoginFilterConfigurer<H>, OneClickLoginAuthenticationFilter, ExtensionLoginFilterSecurityConfigurer<H>> {
    private OneClickUserDetailsService oneClickUserDetailsService;
    private OneClickLoginService oneClickLoginService;

    public OneClickExtensionLoginFilterConfigurer(ExtensionLoginFilterSecurityConfigurer<H> securityConfigurer) {
        super(securityConfigurer, new OneClickLoginAuthenticationFilter(), "/login/oneclick");
    }

    public OneClickExtensionLoginFilterConfigurer<H> oneClickUserDetailsService(OneClickUserDetailsService oneClickUserDetailsService) {
        this.oneClickUserDetailsService = oneClickUserDetailsService;
        return this;
    }

    public OneClickExtensionLoginFilterConfigurer<H> oneClickLoginService(OneClickLoginService oneClickLoginService) {
        this.oneClickLoginService = oneClickLoginService;
        return this;
    }

    public OneClickExtensionLoginFilterConfigurer<H> jwtTokenGenerator(JwtTokenGenerator jwtTokenGenerator) {
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
        ObjectProvider<OneClickUserDetailsService> oneClickUserDetailsServiceObjectProvider = OAuth2AuthorizationUtils.getBeanProvider(applicationContext, OneClickUserDetailsService.class);
        ObjectProvider<OneClickLoginService> oneClickLoginServiceObjectProvider = OAuth2AuthorizationUtils.getBeanProvider(applicationContext, OneClickLoginService.class);
        OneClickUserDetailsService oneClickUserDetailsService = this.oneClickUserDetailsService != null ? this.oneClickUserDetailsService : (OneClickUserDetailsService)oneClickUserDetailsServiceObjectProvider.getIfAvailable(DefaultOneClickUserDetailsService::new);
        Assert.notNull((Object)oneClickUserDetailsService, (String)"oneClickUserDetailsService is required");
        OneClickLoginService oneClickLoginService = this.oneClickLoginService != null ? this.oneClickLoginService : (OneClickLoginService)oneClickLoginServiceObjectProvider.getIfAvailable(DefaultOneClickLoginService::new);
        Assert.notNull((Object)oneClickLoginService, (String)"oneClickLoginService is required");
        OneClickLoginAuthenticationFilter oneClickLoginAuthenticationFilter = (OneClickLoginAuthenticationFilter)((Object)this.getAuthenticationFilter());
        OAuth2AuthorizationUtils.oAuth2AuthenticationProperties(applicationContext).ifAvailable(properties -> {
            OAuth2AuthenticationProperties.ExtensionLogin.OneClickLogin oneClickLogin = properties.getExtensionLogin().getOneClickLogin();
            oneClickLoginAuthenticationFilter.setOtherParamNames(oneClickLogin.getOtherParamNames());
            oneClickLoginAuthenticationFilter.setTokenParamName(oneClickLogin.getTokenParamName());
            oneClickLoginAuthenticationFilter.setOneClickLoginService(oneClickLoginService);
            oneClickLoginAuthenticationFilter.setFilterProcessesUrl(oneClickLogin.getLoginUrl());
        });
        return new OneClickLoginAuthenticationProvider(oneClickUserDetailsService, oneClickLoginService);
    }
}

