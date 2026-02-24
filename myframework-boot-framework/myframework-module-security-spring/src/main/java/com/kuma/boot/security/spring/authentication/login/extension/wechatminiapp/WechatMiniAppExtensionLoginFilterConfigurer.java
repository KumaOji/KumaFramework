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
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class WechatMiniAppExtensionLoginFilterConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractExtensionLoginFilterConfigurer<
        H,
        WechatMiniAppExtensionLoginFilterConfigurer<H>,
        com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.WechatMiniAppAuthenticationFilter,
        ExtensionLoginFilterSecurityConfigurer<H>> {

    private WechatMiniAppUserDetailsService wechatMiniAppUserDetailsService;

    private WechatMiniAppClientService wechatMiniAppClientService;

    private WechatMiniAppSessionKeyCacheService wechatMiniAppSessionKeyCacheService;

    public WechatMiniAppExtensionLoginFilterConfigurer(
            ExtensionLoginFilterSecurityConfigurer<H> securityConfigurer) {
        super(securityConfigurer, new com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.WechatMiniAppAuthenticationFilter(), "/login/miniapp");
    }

    public WechatMiniAppExtensionLoginFilterConfigurer<H> miniAppUserDetailsService(
            WechatMiniAppUserDetailsService wechatMiniAppUserDetailsService) {
        this.wechatMiniAppUserDetailsService = wechatMiniAppUserDetailsService;
        return this;
    }

    public WechatMiniAppExtensionLoginFilterConfigurer<H> miniAppClientService(
            WechatMiniAppClientService wechatMiniAppClientService) {
        this.wechatMiniAppClientService = wechatMiniAppClientService;
        return this;
    }

    public WechatMiniAppExtensionLoginFilterConfigurer<H> miniAppSessionKeyCacheService(
            WechatMiniAppSessionKeyCacheService wechatMiniAppSessionKeyCacheService) {
        this.wechatMiniAppSessionKeyCacheService = wechatMiniAppSessionKeyCacheService;
        return this;
    }

    public WechatMiniAppExtensionLoginFilterConfigurer<H> jwtTokenGenerator(
            JwtTokenGenerator jwtTokenGenerator) {
        this.setJwtTokenGenerator(jwtTokenGenerator);
        return this;
    }

    @Override
    public void configure(H http)  {
        super.configure(http);

        initPreAuthenticationFilter(http);
    }

    private void initPreAuthenticationFilter(H http) {
        ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);
        WechatMiniAppClientService wechatMiniAppClientService =
                this.wechatMiniAppClientService != null
                        ? this.wechatMiniAppClientService
                        : new DefaultWechatWechatMiniAppClientService();

        WechatMiniAppSessionKeyCacheService wechatMiniAppSessionKeyCacheService =
                this.wechatMiniAppSessionKeyCacheService != null
                        ? this.wechatMiniAppSessionKeyCacheService
                        : new DefaultWechatWechatMiniAppSessionKeyCacheService();

        WechatMiniAppPreAuthenticationFilter wechatMiniAppPreAuthenticationFilter =
                new WechatMiniAppPreAuthenticationFilter(
                        wechatMiniAppClientService, wechatMiniAppSessionKeyCacheService);
        http.addFilterBefore(postProcess(wechatMiniAppPreAuthenticationFilter), LogoutFilter.class);
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return PathPatternRequestMatcher.withDefaults()
                .matcher(HttpMethod.POST, loginProcessingUrl);
    }

    @Override
    protected AuthenticationProvider authenticationProvider(H http) {
        ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);
        WechatMiniAppUserDetailsService wechatMiniAppUserDetailsService =
                this.wechatMiniAppUserDetailsService != null
                        ? this.wechatMiniAppUserDetailsService
                        : new DefaultWechatWechatMiniAppUserDetailsService();

        WechatMiniAppSessionKeyCacheService wechatMiniAppSessionKeyCacheService =
                this.wechatMiniAppSessionKeyCacheService != null
                        ? this.wechatMiniAppSessionKeyCacheService
                        : new DefaultWechatWechatMiniAppSessionKeyCacheService();

        return new com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.WechatMiniAppAuthenticationProvider(
                wechatMiniAppUserDetailsService, wechatMiniAppSessionKeyCacheService);
    }
}
