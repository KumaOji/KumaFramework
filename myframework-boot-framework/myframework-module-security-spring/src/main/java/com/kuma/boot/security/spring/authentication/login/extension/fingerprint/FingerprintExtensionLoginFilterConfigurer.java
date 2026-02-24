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

package com.kuma.boot.security.spring.authentication.login.extension.fingerprint;

import static com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils.getBeanProvider;
import static com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils.oAuth2AuthenticationProperties;

import com.kuma.boot.security.spring.authentication.login.extension.AbstractExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.ExtensionLoginFilterSecurityConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.fingerprint.service.DefaultFingerprintUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.extension.fingerprint.service.FingerprintUserDetailsService;
import com.kuma.boot.security.spring.oauth2.token.JwtTokenGenerator;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2AuthenticationProperties.ExtensionLogin.FingerprintLogin;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

public class FingerprintExtensionLoginFilterConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractExtensionLoginFilterConfigurer<
        H,
        FingerprintExtensionLoginFilterConfigurer<H>,
        com.kuma.boot.security.spring.authentication.login.extension.fingerprint.FingerprintAuthenticationFilter,
        ExtensionLoginFilterSecurityConfigurer<H>> {

    private FingerprintUserDetailsService fingerprintUserDetailsService;

    public FingerprintExtensionLoginFilterConfigurer(
            ExtensionLoginFilterSecurityConfigurer<H> securityConfigurer) {
        super(securityConfigurer, new com.kuma.boot.security.spring.authentication.login.extension.fingerprint.FingerprintAuthenticationFilter(), "/login/fingerprint");
    }

    public FingerprintExtensionLoginFilterConfigurer<H> fingerprintUserDetailsService(
            FingerprintUserDetailsService fingerprintUserDetailsService) {
        this.fingerprintUserDetailsService = fingerprintUserDetailsService;
        return this;
    }

    public FingerprintExtensionLoginFilterConfigurer<H> jwtTokenGenerator(
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

        ObjectProvider<FingerprintUserDetailsService> fingerprintUserDetailsServiceObjectProvider =
                getBeanProvider(applicationContext, FingerprintUserDetailsService.class);

        FingerprintUserDetailsService fingerprintUserDetailsService =
                this.fingerprintUserDetailsService != null
                        ? this.fingerprintUserDetailsService
                        : fingerprintUserDetailsServiceObjectProvider.getIfAvailable(
                        DefaultFingerprintUserDetailsService::new);
        Assert.notNull(fingerprintUserDetailsService, "fingerprintUserDetailsService is required");

        com.kuma.boot.security.spring.authentication.login.extension.fingerprint.FingerprintAuthenticationFilter fingerprintAuthenticationFilter =
                this.getAuthenticationFilter();
        oAuth2AuthenticationProperties(applicationContext)
                .ifAvailable(
                        (properties) -> {
                            FingerprintLogin fingerprintLogin =
                                    properties.getExtensionLogin().getFingerprintLogin();
                            fingerprintAuthenticationFilter.setUsernameParameter(
                                    fingerprintLogin.getUsernameParameter());
                            fingerprintAuthenticationFilter.setFingerPrintParameter(
                                    fingerprintLogin.getFingerPrintParameter());
                            fingerprintAuthenticationFilter.setFilterProcessesUrl(
                                    fingerprintLogin.getLoginUrl());
                        });

        return new com.kuma.boot.security.spring.authentication.login.extension.fingerprint.FingerprintAuthenticationProvider(fingerprintUserDetailsService);
    }
}
