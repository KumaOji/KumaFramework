/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.security.spring.authentication.login.extension.face;

import static com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils.getBeanProvider;
import static com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils.oAuth2AuthenticationProperties;

import com.kuma.boot.security.spring.authentication.login.extension.AbstractExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.ExtensionLoginFilterSecurityConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.face.service.DefaultFaceCheckService;
import com.kuma.boot.security.spring.authentication.login.extension.face.service.DefaultFaceUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.extension.face.service.FaceCheckService;
import com.kuma.boot.security.spring.authentication.login.extension.face.service.FaceUserDetailsService;
import com.kuma.boot.security.spring.oauth2.token.JwtTokenGenerator;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2AuthenticationProperties.ExtensionLogin.FaceLogin;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

public class FaceExtensionLoginFilterConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractExtensionLoginFilterConfigurer<
        H,
        FaceExtensionLoginFilterConfigurer<H>,
        com.kuma.boot.security.spring.authentication.login.extension.face.FaceAuthenticationFilter,
        ExtensionLoginFilterSecurityConfigurer<H>> {

    private FaceUserDetailsService faceUserDetailsService;
    private FaceCheckService faceCheckService;

    public FaceExtensionLoginFilterConfigurer(
            ExtensionLoginFilterSecurityConfigurer<H> securityConfigurer) {
        super(securityConfigurer, new com.kuma.boot.security.spring.authentication.login.extension.face.FaceAuthenticationFilter(), "/login/face");
    }

    public FaceExtensionLoginFilterConfigurer<H> faceUserDetailsService(
            FaceUserDetailsService faceUserDetailsService) {
        this.faceUserDetailsService = faceUserDetailsService;
        return this;
    }

    public FaceExtensionLoginFilterConfigurer<H> faceCheckService(
            FaceCheckService faceCheckService) {
        this.faceCheckService = faceCheckService;
        return this;
    }

    public FaceExtensionLoginFilterConfigurer<H> jwtTokenGenerator(
            JwtTokenGenerator jwtTokenGenerator) {
        this.setJwtTokenGenerator(jwtTokenGenerator);
        return this;
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return PathPatternRequestMatcher.withDefaults()
                .matcher(HttpMethod.POST, loginProcessingUrl);
    }

    @Override
    protected AuthenticationProvider authenticationProvider(H http) {
        ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);

        ObjectProvider<FaceUserDetailsService> faceUserDetailsServiceObjectProvider =
                getBeanProvider(applicationContext, FaceUserDetailsService.class);
        FaceUserDetailsService faceUserDetailsService =
                this.faceUserDetailsService != null
                        ? this.faceUserDetailsService
                        : faceUserDetailsServiceObjectProvider.getIfAvailable(
                        DefaultFaceUserDetailsService::new);
        Assert.notNull(faceUserDetailsService, "FaceUserDetailsService is required");

        ObjectProvider<FaceCheckService> faceCheckServiceObjectProvider =
                getBeanProvider(applicationContext, FaceCheckService.class);
        FaceCheckService faceCheckService =
                this.faceCheckService != null
                        ? this.faceCheckService
                        : faceCheckServiceObjectProvider.getIfAvailable(
                        DefaultFaceCheckService::new);
        Assert.notNull(faceCheckService, "FaceCheckService is required");

        com.kuma.boot.security.spring.authentication.login.extension.face.FaceAuthenticationFilter faceAuthenticationFilter = this.getAuthenticationFilter();
        oAuth2AuthenticationProperties(applicationContext)
                .ifAvailable(
                        (properties) -> {
                            FaceLogin faceLogin = properties.getExtensionLogin().getFaceLogin();
                            faceAuthenticationFilter.setImgBase64Parameter(
                                    faceLogin.getImgBase64Parameter());
                            faceAuthenticationFilter.setFilterProcessesUrl(faceLogin.getLoginUrl());
                        });

        return new com.kuma.boot.security.spring.authentication.login.extension.face.FaceAuthenticationProvider(faceUserDetailsService, faceCheckService);
    }
}
