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

package com.kuma.boot.security.spring.authentication.login.extension.captcha;

import static com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils.getBeanProvider;
import static com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils.oAuth2AuthenticationProperties;

import com.kuma.boot.security.spring.authentication.login.extension.AbstractExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.ExtensionLoginFilterSecurityConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.captcha.service.CaptchaCheckService;
import com.kuma.boot.security.spring.authentication.login.extension.captcha.service.CaptchaUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.extension.captcha.service.DefaultCaptchaCheckService;
import com.kuma.boot.security.spring.authentication.login.extension.captcha.service.DefaultCaptchaUserDetailsService;
import com.kuma.boot.security.spring.oauth2.token.JwtTokenGenerator;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2AuthenticationProperties.ExtensionLogin.CaptchaLogin;
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
        extends AbstractExtensionLoginFilterConfigurer<
        H,
        CaptchaExtensionLoginFilterConfigurer<H>,
        com.kuma.boot.security.spring.authentication.login.extension.captcha.CaptchaAuthenticationFilter,
        ExtensionLoginFilterSecurityConfigurer<H>> {

    private CaptchaUserDetailsService captchaUserDetailsService;

    private CaptchaCheckService captchaCheckService;

    public CaptchaExtensionLoginFilterConfigurer(
            ExtensionLoginFilterSecurityConfigurer<H> securityConfigurer) {
        super(securityConfigurer, new com.kuma.boot.security.spring.authentication.login.extension.captcha.CaptchaAuthenticationFilter(), "/login/captcha");
    }

    public CaptchaExtensionLoginFilterConfigurer<H> captchaUserDetailsService(
            CaptchaUserDetailsService captchaUserDetailsService) {
        this.captchaUserDetailsService = captchaUserDetailsService;
        return this;
    }

    public CaptchaExtensionLoginFilterConfigurer<H> captchaCheckService(
            CaptchaCheckService captchaCheckService) {
        this.captchaCheckService = captchaCheckService;
        return this;
    }

    public CaptchaExtensionLoginFilterConfigurer<H> jwtTokenGenerator(
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

        CaptchaUserDetailsService captchaUserDetailsService =
                this.captchaUserDetailsService != null
                        ? this.captchaUserDetailsService
                        : getBeanProvider(applicationContext, CaptchaUserDetailsService.class)
                        .getIfAvailable(DefaultCaptchaUserDetailsService::new);
        Assert.notNull(captchaUserDetailsService, "captchaUserDetailsService is required");

        CaptchaCheckService captchaCheckService =
                this.captchaCheckService != null
                        ? this.captchaCheckService
                        : getBeanProvider(applicationContext, CaptchaCheckService.class)
                        .getIfAvailable(DefaultCaptchaCheckService::new);
        Assert.notNull(captchaCheckService, "captchaService is required");

        com.kuma.boot.security.spring.authentication.login.extension.captcha.CaptchaAuthenticationFilter captchaAuthenticationFilter = this.getAuthenticationFilter();
        oAuth2AuthenticationProperties(applicationContext)
                .ifAvailable(
                        (properties) -> {
                            CaptchaLogin captchaLogin =
                                    properties.getExtensionLogin().getCaptchaLogin();
                            captchaAuthenticationFilter.setUsernameParameter(
                                    captchaLogin.getUsernameParameter());
                            captchaAuthenticationFilter.setPasswordParameter(
                                    captchaLogin.getPasswordParameter());
                            captchaAuthenticationFilter.setTypeParameter(
                                    captchaLogin.getTypeParameter());
                            captchaAuthenticationFilter.setVerificationCodeParameter(
                                    captchaLogin.getVerificationCodeParameter());
                            captchaAuthenticationFilter.setFilterProcessesUrl(
                                    captchaLogin.getLoginUrl());
                        });

        com.kuma.boot.security.spring.authentication.login.extension.captcha.CaptchaAuthenticationProvider provider =
                new com.kuma.boot.security.spring.authentication.login.extension.captcha.CaptchaAuthenticationProvider(captchaUserDetailsService, captchaCheckService);
        getBeanProvider(applicationContext, PasswordEncoder.class)
                .ifAvailable(provider::setPasswordEncoder);
        getBeanProvider(applicationContext, UserDetailsChecker.class)
                .ifAvailable(provider::setPostAuthenticationChecks);
        getBeanProvider(applicationContext, MessageSourceAccessor.class)
                .ifAvailable(provider::setMessages);
        return provider;
    }
}
