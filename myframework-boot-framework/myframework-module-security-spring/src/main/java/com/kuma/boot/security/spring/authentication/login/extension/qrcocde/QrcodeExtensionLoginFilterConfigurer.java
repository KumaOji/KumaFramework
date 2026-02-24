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

package com.kuma.boot.security.spring.authentication.login.extension.qrcocde;

import com.kuma.boot.security.spring.authentication.login.extension.AbstractExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.ExtensionLoginFilterSecurityConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.qrcocde.service.DefaultQrcodeService;
import com.kuma.boot.security.spring.authentication.login.extension.qrcocde.service.DefaultQrcodeUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.extension.qrcocde.service.QrcodeService;
import com.kuma.boot.security.spring.authentication.login.extension.qrcocde.service.QrcodeUserDetailsService;
import com.kuma.boot.security.spring.oauth2.token.JwtTokenGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

public class QrcodeExtensionLoginFilterConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractExtensionLoginFilterConfigurer<
        H,
        QrcodeExtensionLoginFilterConfigurer<H>,
        com.kuma.boot.security.spring.authentication.login.extension.qrcocde.QrcodeAuthenticationFilter,
        ExtensionLoginFilterSecurityConfigurer<H>> {

    private QrcodeUserDetailsService qrcodeUserDetailsService;
    private QrcodeService qrcodeService;

    public QrcodeExtensionLoginFilterConfigurer(
            ExtensionLoginFilterSecurityConfigurer<H> securityConfigurer) {
        super(securityConfigurer, new com.kuma.boot.security.spring.authentication.login.extension.qrcocde.QrcodeAuthenticationFilter(), "/login/qrcode");
    }

    public QrcodeExtensionLoginFilterConfigurer<H> accountUserDetailsService(
            QrcodeUserDetailsService qrcodeUserDetailsService) {
        this.qrcodeUserDetailsService = qrcodeUserDetailsService;
        return this;
    }

    public QrcodeExtensionLoginFilterConfigurer<H> qrcodeService(QrcodeService qrcodeService) {
        this.qrcodeService = qrcodeService;
        return this;
    }

    public QrcodeExtensionLoginFilterConfigurer<H> jwtTokenGenerator(
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
        QrcodeUserDetailsService qrcodeUserDetailsService =
                this.qrcodeUserDetailsService != null
                        ? this.qrcodeUserDetailsService
                        : new DefaultQrcodeUserDetailsService();
        Assert.notNull(qrcodeUserDetailsService, "qrcodeUserDetailsService is required");

        QrcodeService qrcodeService =
                this.qrcodeService != null ? this.qrcodeService : new DefaultQrcodeService();
        Assert.notNull(qrcodeService, "captchaUserDetailsService is required");

        return new com.kuma.boot.security.spring.authentication.login.extension.qrcocde.QrcodeAuthenticationProvider(qrcodeService, qrcodeUserDetailsService);
    }
}
