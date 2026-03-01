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

package com.kuma.boot.security.spring.authentication.login.extension.wechatmp;

import com.kuma.boot.security.spring.authentication.login.extension.AbstractExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.ExtensionLoginFilterSecurityConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.wechatmp.service.DefaultWechatWechatMpUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.extension.wechatmp.service.WechatMpUserDetailsService;
import com.kuma.boot.security.spring.oauth2.token.JwtTokenGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

public class WechatMpExtensionLoginFilterConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractExtensionLoginFilterConfigurer<
        H,
        WechatMpExtensionLoginFilterConfigurer<H>,
        com.kuma.boot.security.spring.authentication.login.extension.wechatmp.WechatMpAuthenticationFilter,
        ExtensionLoginFilterSecurityConfigurer<H>> {

    private WechatMpUserDetailsService wechatMpUserDetailsService;

    public WechatMpExtensionLoginFilterConfigurer(
            ExtensionLoginFilterSecurityConfigurer<H> securityConfigurer) {
        super(securityConfigurer, new com.kuma.boot.security.spring.authentication.login.extension.wechatmp.WechatMpAuthenticationFilter(), "/login/mp");
    }

    public WechatMpExtensionLoginFilterConfigurer<H> mpUserDetailsService(
            WechatMpUserDetailsService wechatMpUserDetailsService) {
        this.wechatMpUserDetailsService = wechatMpUserDetailsService;
        return this;
    }

    public WechatMpExtensionLoginFilterConfigurer<H> jwtTokenGenerator(
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

        WechatMpUserDetailsService wechatMpUserDetailsService =
                this.wechatMpUserDetailsService != null
                        ? this.wechatMpUserDetailsService
                        : new DefaultWechatWechatMpUserDetailsService();
        Assert.notNull(wechatMpUserDetailsService, "mpUserDetailsService is required");

        return new com.kuma.boot.security.spring.authentication.login.extension.wechatmp.WechatMpAuthenticationProvider(wechatMpUserDetailsService);
    }
}
