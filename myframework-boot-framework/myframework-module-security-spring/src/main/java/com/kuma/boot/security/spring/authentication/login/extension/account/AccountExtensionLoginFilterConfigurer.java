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

package com.kuma.boot.security.spring.authentication.login.extension.account;

import static com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils.getBeanProvider;
import static com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils.oAuth2AuthenticationProperties;

import com.kuma.boot.security.spring.authentication.login.extension.AbstractExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.ExtensionLoginFilterSecurityConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.account.service.AccountUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.extension.account.service.DefaultAccountUserDetailsService;
import com.kuma.boot.security.spring.oauth2.token.JwtTokenGenerator;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2AuthenticationProperties.ExtensionLogin.AccountLogin;
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

/**
 * 账户登录扩展过滤器配置
 *
 * @author kuma
 * @version 2023.04
 * @since 2023-06-29 13:57:50
 */
public class AccountExtensionLoginFilterConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractExtensionLoginFilterConfigurer<
        H,
        AccountExtensionLoginFilterConfigurer<H>,
        com.kuma.boot.security.spring.authentication.login.extension.account.AccountAuthenticationFilter,
        ExtensionLoginFilterSecurityConfigurer<H>> {

    private AccountUserDetailsService accountUserDetailsService;

    public AccountExtensionLoginFilterConfigurer(
            ExtensionLoginFilterSecurityConfigurer<H> securityConfigurer) {
        super(securityConfigurer, new com.kuma.boot.security.spring.authentication.login.extension.account.AccountAuthenticationFilter(), "/login/account");
    }

    public AccountExtensionLoginFilterConfigurer<H> accountUserDetailsService(
            AccountUserDetailsService accountUserDetailsService) {
        this.accountUserDetailsService = accountUserDetailsService;
        return this;
    }

    public AccountExtensionLoginFilterConfigurer<H> jwtTokenGenerator(
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

        // 优先级 代码设置的accountUserDetailsService  > accountUserDetailsService类型bean >
        // DefaultAccountUserDetailsService
        AccountUserDetailsService accountUserDetailsService =
                this.accountUserDetailsService != null
                        ? this.accountUserDetailsService
                        : getBeanProvider(applicationContext, AccountUserDetailsService.class)
                        .getIfAvailable(DefaultAccountUserDetailsService::new);
        Assert.notNull(accountUserDetailsService, "accountUserDetailsService is required");

        com.kuma.boot.security.spring.authentication.login.extension.account.AccountAuthenticationFilter authenticationFilter = this.getAuthenticationFilter();
        SecurityContextRepository securityContextRepository =
                new DelegatingSecurityContextRepository(
                        new RequestAttributeSecurityContextRepository(),
                        new HttpSessionSecurityContextRepository());
        authenticationFilter.setSecurityContextRepository(securityContextRepository);

        oAuth2AuthenticationProperties(applicationContext)
                .ifAvailable(
                        (properties) -> {
                            AccountLogin accountLogin =
                                    properties.getExtensionLogin().getAccountLogin();
                            authenticationFilter.setUsernameParameter(
                                    accountLogin.getUsernameParameter());
                            authenticationFilter.setPasswordParameter(
                                    accountLogin.getPasswordParameter());
                            authenticationFilter.setTypeParameter(accountLogin.getTypeParameter());
                            authenticationFilter.setFilterProcessesUrl(accountLogin.getLoginUrl());
                        });

        com.kuma.boot.security.spring.authentication.login.extension.account.AccountAuthenticationProvider provider =
                new com.kuma.boot.security.spring.authentication.login.extension.account.AccountAuthenticationProvider(accountUserDetailsService);
        getBeanProvider(applicationContext, PasswordEncoder.class)
                .ifAvailable(provider::setPasswordEncoder);
        return provider;
    }
}
