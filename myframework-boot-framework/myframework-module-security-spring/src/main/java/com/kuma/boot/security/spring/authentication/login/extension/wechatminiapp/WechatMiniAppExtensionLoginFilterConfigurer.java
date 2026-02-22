/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.servlet.Filter
 *  org.springframework.context.ApplicationContext
 *  org.springframework.http.HttpMethod
 *  org.springframework.security.authentication.AuthenticationProvider
 *  org.springframework.security.config.annotation.web.HttpSecurityBuilder
 *  org.springframework.security.web.authentication.logout.LogoutFilter
 *  org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
 *  org.springframework.security.web.util.matcher.RequestMatcher
 */
package com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp;

import com.kuma.boot.security.spring.authentication.login.extension.AbstractExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.ExtensionLoginFilterSecurityConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.service.DefaultWechatWechatMiniAppClientService;
import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.service.DefaultWechatWechatMiniAppSessionKeyCacheService;
import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.service.DefaultWechatWechatMiniAppUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.service.WechatMiniAppClientService;
import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.service.WechatMiniAppSessionKeyCacheService;
import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.service.WechatMiniAppUserDetailsService;
import com.kuma.boot.security.spring.oauth2.token.JwtTokenGenerator;
import jakarta.servlet.Filter;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class WechatMiniAppExtensionLoginFilterConfigurer<H extends HttpSecurityBuilder<H>>
extends AbstractExtensionLoginFilterConfigurer<H, WechatMiniAppExtensionLoginFilterConfigurer<H>, WechatMiniAppAuthenticationFilter, ExtensionLoginFilterSecurityConfigurer<H>> {
    private WechatMiniAppUserDetailsService wechatMiniAppUserDetailsService;
    private WechatMiniAppClientService wechatMiniAppClientService;
    private WechatMiniAppSessionKeyCacheService wechatMiniAppSessionKeyCacheService;

    public WechatMiniAppExtensionLoginFilterConfigurer(ExtensionLoginFilterSecurityConfigurer<H> securityConfigurer) {
        super(securityConfigurer, new WechatMiniAppAuthenticationFilter(), "/login/miniapp");
    }

    public WechatMiniAppExtensionLoginFilterConfigurer<H> miniAppUserDetailsService(WechatMiniAppUserDetailsService wechatMiniAppUserDetailsService) {
        this.wechatMiniAppUserDetailsService = wechatMiniAppUserDetailsService;
        return this;
    }

    public WechatMiniAppExtensionLoginFilterConfigurer<H> miniAppClientService(WechatMiniAppClientService wechatMiniAppClientService) {
        this.wechatMiniAppClientService = wechatMiniAppClientService;
        return this;
    }

    public WechatMiniAppExtensionLoginFilterConfigurer<H> miniAppSessionKeyCacheService(WechatMiniAppSessionKeyCacheService wechatMiniAppSessionKeyCacheService) {
        this.wechatMiniAppSessionKeyCacheService = wechatMiniAppSessionKeyCacheService;
        return this;
    }

    public WechatMiniAppExtensionLoginFilterConfigurer<H> jwtTokenGenerator(JwtTokenGenerator jwtTokenGenerator) {
        this.setJwtTokenGenerator(jwtTokenGenerator);
        return this;
    }

    @Override
    public void configure(H http) throws Exception {
        super.configure(http);
        this.initPreAuthenticationFilter(http);
    }

    private void initPreAuthenticationFilter(H http) {
        ApplicationContext applicationContext = (ApplicationContext)http.getSharedObject(ApplicationContext.class);
        WechatMiniAppClientService wechatMiniAppClientService = this.wechatMiniAppClientService != null ? this.wechatMiniAppClientService : new DefaultWechatWechatMiniAppClientService();
        WechatMiniAppSessionKeyCacheService wechatMiniAppSessionKeyCacheService = this.wechatMiniAppSessionKeyCacheService != null ? this.wechatMiniAppSessionKeyCacheService : new DefaultWechatWechatMiniAppSessionKeyCacheService();
        WechatMiniAppPreAuthenticationFilter wechatMiniAppPreAuthenticationFilter = new WechatMiniAppPreAuthenticationFilter(wechatMiniAppClientService, wechatMiniAppSessionKeyCacheService);
        http.addFilterBefore((Filter)this.postProcess((Object)wechatMiniAppPreAuthenticationFilter), LogoutFilter.class);
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, loginProcessingUrl);
    }

    @Override
    protected AuthenticationProvider authenticationProvider(H http) {
        ApplicationContext applicationContext = (ApplicationContext)http.getSharedObject(ApplicationContext.class);
        WechatMiniAppUserDetailsService wechatMiniAppUserDetailsService = this.wechatMiniAppUserDetailsService != null ? this.wechatMiniAppUserDetailsService : new DefaultWechatWechatMiniAppUserDetailsService();
        WechatMiniAppSessionKeyCacheService wechatMiniAppSessionKeyCacheService = this.wechatMiniAppSessionKeyCacheService != null ? this.wechatMiniAppSessionKeyCacheService : new DefaultWechatWechatMiniAppSessionKeyCacheService();
        return new WechatMiniAppAuthenticationProvider(wechatMiniAppUserDetailsService, wechatMiniAppSessionKeyCacheService);
    }
}

