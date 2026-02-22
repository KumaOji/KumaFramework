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
package com.kuma.boot.security.spring.authentication.login.extension.fingerprint;

import com.kuma.boot.security.spring.authentication.login.extension.AbstractExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.ExtensionLoginFilterSecurityConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.fingerprint.service.DefaultFingerprintUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.extension.fingerprint.service.FingerprintUserDetailsService;
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

public class FingerprintExtensionLoginFilterConfigurer<H extends HttpSecurityBuilder<H>>
extends AbstractExtensionLoginFilterConfigurer<H, FingerprintExtensionLoginFilterConfigurer<H>, FingerprintAuthenticationFilter, ExtensionLoginFilterSecurityConfigurer<H>> {
    private FingerprintUserDetailsService fingerprintUserDetailsService;

    public FingerprintExtensionLoginFilterConfigurer(ExtensionLoginFilterSecurityConfigurer<H> securityConfigurer) {
        super(securityConfigurer, new FingerprintAuthenticationFilter(), "/login/fingerprint");
    }

    public FingerprintExtensionLoginFilterConfigurer<H> fingerprintUserDetailsService(FingerprintUserDetailsService fingerprintUserDetailsService) {
        this.fingerprintUserDetailsService = fingerprintUserDetailsService;
        return this;
    }

    public FingerprintExtensionLoginFilterConfigurer<H> jwtTokenGenerator(JwtTokenGenerator jwtTokenGenerator) {
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
        ObjectProvider<FingerprintUserDetailsService> fingerprintUserDetailsServiceObjectProvider = OAuth2AuthorizationUtils.getBeanProvider(applicationContext, FingerprintUserDetailsService.class);
        FingerprintUserDetailsService fingerprintUserDetailsService = this.fingerprintUserDetailsService != null ? this.fingerprintUserDetailsService : (FingerprintUserDetailsService)fingerprintUserDetailsServiceObjectProvider.getIfAvailable(DefaultFingerprintUserDetailsService::new);
        Assert.notNull((Object)fingerprintUserDetailsService, (String)"fingerprintUserDetailsService is required");
        FingerprintAuthenticationFilter fingerprintAuthenticationFilter = (FingerprintAuthenticationFilter)((Object)this.getAuthenticationFilter());
        OAuth2AuthorizationUtils.oAuth2AuthenticationProperties(applicationContext).ifAvailable(properties -> {
            OAuth2AuthenticationProperties.ExtensionLogin.FingerprintLogin fingerprintLogin = properties.getExtensionLogin().getFingerprintLogin();
            fingerprintAuthenticationFilter.setUsernameParameter(fingerprintLogin.getUsernameParameter());
            fingerprintAuthenticationFilter.setFingerPrintParameter(fingerprintLogin.getFingerPrintParameter());
            fingerprintAuthenticationFilter.setFilterProcessesUrl(fingerprintLogin.getLoginUrl());
        });
        return new FingerprintAuthenticationProvider(fingerprintUserDetailsService);
    }
}

