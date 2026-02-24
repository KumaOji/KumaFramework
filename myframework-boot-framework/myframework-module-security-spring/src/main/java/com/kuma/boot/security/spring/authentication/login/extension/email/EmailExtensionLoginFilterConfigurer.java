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

package com.kuma.boot.security.spring.authentication.login.extension.email;

import static com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils.getBeanProvider;
import static com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils.oAuth2AuthenticationProperties;

import com.kuma.boot.security.spring.authentication.login.extension.AbstractExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.ExtensionLoginFilterSecurityConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.email.service.DefaultEmailCheckService;
import com.kuma.boot.security.spring.authentication.login.extension.email.service.DefaultEmailUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.extension.email.service.EmailCheckService;
import com.kuma.boot.security.spring.authentication.login.extension.email.service.EmailUserDetailsService;
import com.kuma.boot.security.spring.oauth2.token.JwtTokenGenerator;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2AuthenticationProperties.ExtensionLogin.EmailLogin;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

/**
 * 电子邮件扩展登录过滤器配置程序
 *
 * @author kuma
 * @version 2023.07
 * @see AbstractExtensionLoginFilterConfigurer
 * @since 2023-07-13 12:03:18
 */
public class EmailExtensionLoginFilterConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractExtensionLoginFilterConfigurer<
        H,
        EmailExtensionLoginFilterConfigurer<H>,
        com.kuma.boot.security.spring.authentication.login.extension.email.EmailAuthenticationFilter,
        ExtensionLoginFilterSecurityConfigurer<H>> {

    private EmailUserDetailsService emailUserDetailsService;
    private EmailCheckService emailCheckService;

    public EmailExtensionLoginFilterConfigurer(
            ExtensionLoginFilterSecurityConfigurer<H> securityConfigurer) {
        super(securityConfigurer, new com.kuma.boot.security.spring.authentication.login.extension.email.EmailAuthenticationFilter(), "/login/email");
    }

    public EmailExtensionLoginFilterConfigurer<H> emailUserDetailsService(
            EmailUserDetailsService emailUserDetailsService) {
        this.emailUserDetailsService = emailUserDetailsService;
        return this;
    }

    public EmailExtensionLoginFilterConfigurer<H> emailCheckService(
            EmailCheckService emailCheckService) {
        this.emailCheckService = emailCheckService;
        return this;
    }

    public EmailExtensionLoginFilterConfigurer<H> jwtTokenGenerator(
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

        ObjectProvider<EmailUserDetailsService> emailUserDetailsServiceObjectProvider =
                getBeanProvider(applicationContext, EmailUserDetailsService.class);
        EmailUserDetailsService emailUserDetailsService =
                this.emailUserDetailsService != null
                        ? this.emailUserDetailsService
                        : emailUserDetailsServiceObjectProvider.getIfAvailable(
                        DefaultEmailUserDetailsService::new);
        Assert.notNull(emailUserDetailsService, "emailUserDetailsService is required");

        ObjectProvider<EmailCheckService> emailCheckServiceObjectProvider =
                getBeanProvider(applicationContext, EmailCheckService.class);
        EmailCheckService emailCheckService =
                this.emailCheckService != null
                        ? this.emailCheckService
                        : emailCheckServiceObjectProvider.getIfAvailable(
                        DefaultEmailCheckService::new);
        Assert.notNull(emailCheckService, "emailCheckService is required");

        com.kuma.boot.security.spring.authentication.login.extension.email.EmailAuthenticationFilter emailAuthenticationFilter = this.getAuthenticationFilter();
        oAuth2AuthenticationProperties(applicationContext)
                .ifAvailable(
                        (properties) -> {
                            EmailLogin emailLogin = properties.getExtensionLogin().getEmailLogin();
                            emailAuthenticationFilter.setEmailParameter(
                                    emailLogin.getEmailParameter());
                            emailAuthenticationFilter.setEmailCodeParameter(
                                    emailLogin.getEmailCodeParameter());
                            emailAuthenticationFilter.setFilterProcessesUrl(
                                    emailLogin.getLoginUrl());
                        });

        return new com.kuma.boot.security.spring.authentication.login.extension.email.EmailAuthenticationProvider(emailUserDetailsService, emailCheckService);
    }
}
