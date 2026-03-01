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

package com.kuma.boot.security.spring.authentication.login.extension.oneClick;

import static com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils.getBeanProvider;
import static com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils.oAuth2AuthenticationProperties;

import com.kuma.boot.security.spring.authentication.login.extension.AbstractExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.ExtensionLoginFilterSecurityConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.oneClick.service.DefaultOneClickLoginService;
import com.kuma.boot.security.spring.authentication.login.extension.oneClick.service.DefaultOneClickUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.extension.oneClick.service.OneClickLoginService;
import com.kuma.boot.security.spring.authentication.login.extension.oneClick.service.OneClickUserDetailsService;
import com.kuma.boot.security.spring.oauth2.token.JwtTokenGenerator;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2AuthenticationProperties.ExtensionLogin.OneClickLogin;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

/**
 * 一键登录过滤器配置
 *
 * @author kuma
 * @version 2023.04
 * @since 2023-06-16 14:51:54
 */
public class OneClickExtensionLoginFilterConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractExtensionLoginFilterConfigurer<
        H,
        OneClickExtensionLoginFilterConfigurer<H>,
        OneClickLoginAuthenticationFilter,
        ExtensionLoginFilterSecurityConfigurer<H>> {

    private OneClickUserDetailsService oneClickUserDetailsService;
    private OneClickLoginService oneClickLoginService;

    public OneClickExtensionLoginFilterConfigurer(
            ExtensionLoginFilterSecurityConfigurer<H> securityConfigurer) {
        super(securityConfigurer, new OneClickLoginAuthenticationFilter(), "/login/oneclick");
    }

    public OneClickExtensionLoginFilterConfigurer<H> oneClickUserDetailsService(
            OneClickUserDetailsService oneClickUserDetailsService) {
        this.oneClickUserDetailsService = oneClickUserDetailsService;
        return this;
    }

    public OneClickExtensionLoginFilterConfigurer<H> oneClickLoginService(
            OneClickLoginService oneClickLoginService) {
        this.oneClickLoginService = oneClickLoginService;
        return this;
    }

    public OneClickExtensionLoginFilterConfigurer<H> jwtTokenGenerator(
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
        ObjectProvider<OneClickUserDetailsService> oneClickUserDetailsServiceObjectProvider =
                getBeanProvider(applicationContext, OneClickUserDetailsService.class);
        ObjectProvider<OneClickLoginService> oneClickLoginServiceObjectProvider =
                getBeanProvider(applicationContext, OneClickLoginService.class);

        OneClickUserDetailsService oneClickUserDetailsService =
                this.oneClickUserDetailsService != null
                        ? this.oneClickUserDetailsService
                        : oneClickUserDetailsServiceObjectProvider.getIfAvailable(
                        DefaultOneClickUserDetailsService::new);
        Assert.notNull(oneClickUserDetailsService, "oneClickUserDetailsService is required");

        OneClickLoginService oneClickLoginService =
                this.oneClickLoginService != null
                        ? this.oneClickLoginService
                        : oneClickLoginServiceObjectProvider.getIfAvailable(
                        DefaultOneClickLoginService::new);
        Assert.notNull(oneClickLoginService, "oneClickLoginService is required");

        OneClickLoginAuthenticationFilter oneClickLoginAuthenticationFilter =
                this.getAuthenticationFilter();
        oAuth2AuthenticationProperties(applicationContext)
                .ifAvailable(
                        (properties) -> {
                            OneClickLogin oneClickLogin =
                                    properties.getExtensionLogin().getOneClickLogin();
                            oneClickLoginAuthenticationFilter.setOtherParamNames(
                                    oneClickLogin.getOtherParamNames());
                            oneClickLoginAuthenticationFilter.setTokenParamName(
                                    oneClickLogin.getTokenParamName());
                            oneClickLoginAuthenticationFilter.setOneClickLoginService(
                                    oneClickLoginService);
                            oneClickLoginAuthenticationFilter.setFilterProcessesUrl(
                                    oneClickLogin.getLoginUrl());
                        });

        return new OneClickLoginAuthenticationProvider(
                oneClickUserDetailsService, oneClickLoginService);
    }
}
