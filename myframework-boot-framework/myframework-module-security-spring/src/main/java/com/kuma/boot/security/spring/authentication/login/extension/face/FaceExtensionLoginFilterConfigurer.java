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
package com.kuma.boot.security.spring.authentication.login.extension.face;

import com.kuma.boot.security.spring.authentication.login.extension.AbstractExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.ExtensionLoginFilterSecurityConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.face.service.DefaultFaceCheckService;
import com.kuma.boot.security.spring.authentication.login.extension.face.service.DefaultFaceUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.extension.face.service.FaceCheckService;
import com.kuma.boot.security.spring.authentication.login.extension.face.service.FaceUserDetailsService;
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

public class FaceExtensionLoginFilterConfigurer<H extends HttpSecurityBuilder<H>>
extends AbstractExtensionLoginFilterConfigurer<H, FaceExtensionLoginFilterConfigurer<H>, FaceAuthenticationFilter, ExtensionLoginFilterSecurityConfigurer<H>> {
    private FaceUserDetailsService faceUserDetailsService;
    private FaceCheckService faceCheckService;

    public FaceExtensionLoginFilterConfigurer(ExtensionLoginFilterSecurityConfigurer<H> securityConfigurer) {
        super(securityConfigurer, new FaceAuthenticationFilter(), "/login/face");
    }

    public FaceExtensionLoginFilterConfigurer<H> faceUserDetailsService(FaceUserDetailsService faceUserDetailsService) {
        this.faceUserDetailsService = faceUserDetailsService;
        return this;
    }

    public FaceExtensionLoginFilterConfigurer<H> faceCheckService(FaceCheckService faceCheckService) {
        this.faceCheckService = faceCheckService;
        return this;
    }

    public FaceExtensionLoginFilterConfigurer<H> jwtTokenGenerator(JwtTokenGenerator jwtTokenGenerator) {
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
        ObjectProvider<FaceUserDetailsService> faceUserDetailsServiceObjectProvider = OAuth2AuthorizationUtils.getBeanProvider(applicationContext, FaceUserDetailsService.class);
        FaceUserDetailsService faceUserDetailsService = this.faceUserDetailsService != null ? this.faceUserDetailsService : (FaceUserDetailsService)faceUserDetailsServiceObjectProvider.getIfAvailable(DefaultFaceUserDetailsService::new);
        Assert.notNull((Object)faceUserDetailsService, (String)"FaceUserDetailsService is required");
        ObjectProvider<FaceCheckService> faceCheckServiceObjectProvider = OAuth2AuthorizationUtils.getBeanProvider(applicationContext, FaceCheckService.class);
        FaceCheckService faceCheckService = this.faceCheckService != null ? this.faceCheckService : (FaceCheckService)faceCheckServiceObjectProvider.getIfAvailable(DefaultFaceCheckService::new);
        Assert.notNull((Object)faceCheckService, (String)"FaceCheckService is required");
        FaceAuthenticationFilter faceAuthenticationFilter = (FaceAuthenticationFilter)((Object)this.getAuthenticationFilter());
        OAuth2AuthorizationUtils.oAuth2AuthenticationProperties(applicationContext).ifAvailable(properties -> {
            OAuth2AuthenticationProperties.ExtensionLogin.FaceLogin faceLogin = properties.getExtensionLogin().getFaceLogin();
            faceAuthenticationFilter.setImgBase64Parameter(faceLogin.getImgBase64Parameter());
            faceAuthenticationFilter.setFilterProcessesUrl(faceLogin.getLoginUrl());
        });
        return new FaceAuthenticationProvider(faceUserDetailsService, faceCheckService);
    }
}

