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

package com.kuma.boot.security.spring.authentication.login.form.qrcode;

import static com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils.getBeanProvider;
import static com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils.oAuth2AuthenticationProperties;

import com.kuma.boot.security.spring.authentication.login.form.FormLoginWebAuthenticationDetailSource;
import com.kuma.boot.security.spring.authentication.login.form.qrcode.service.DefaultFormQrcodeService;
import com.kuma.boot.security.spring.authentication.login.form.qrcode.service.DefaultFormQrcodeUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.form.qrcode.service.FormQrcodeService;
import com.kuma.boot.security.spring.authentication.login.form.qrcode.service.FormQrcodeUserDetailsService;
import com.kuma.boot.security.spring.authentication.response.failure.FormLoginAuthenticationFailureHandler;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2AuthenticationProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * <p>基于spring security 扩展表单登录方式(基于表单请求)</p>
 * <p>
 * 使用此种方式，相当于额外增加了一种表单登录方式。因此对原有的 http.formlogin进行的配置，对当前此种方式的配置并不生效。
 *
 * @see org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer
 * @since : 2022/4/12 13:29
 */
public class FormQrcodeLoginHttpConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractHttpConfigurer<FormQrcodeLoginHttpConfigurer<H>, H> {

    private FormQrcodeService formQrcodeService;
    private FormQrcodeUserDetailsService formQrcodeUserDetailsService;

    @Override
    public void configure(H httpSecurity)  {
        AuthenticationManager authenticationManager =
                httpSecurity.getSharedObject(AuthenticationManager.class);
        SecurityContextRepository securityContextRepository =
                new DelegatingSecurityContextRepository(
                        new RequestAttributeSecurityContextRepository(),
                        new HttpSessionSecurityContextRepository());

        ApplicationContext applicationContext =
                httpSecurity.getSharedObject(ApplicationContext.class);
        ObjectProvider<OAuth2AuthenticationProperties>
                oAuth2AuthenticationPropertiesObjectProvider =
                oAuth2AuthenticationProperties(applicationContext);
        OAuth2AuthenticationProperties authenticationProperties =
                oAuth2AuthenticationPropertiesObjectProvider.getObject();

        com.kuma.boot.security.spring.authentication.login.form.qrcode.FormQrcodeAuthenticationFilter filter =
                new com.kuma.boot.security.spring.authentication.login.form.qrcode.FormQrcodeAuthenticationFilter(authenticationManager);
        filter.setAuthenticationDetailsSource(
                new FormLoginWebAuthenticationDetailSource(authenticationProperties));

        filter.setAuthenticationFailureHandler(
                new FormLoginAuthenticationFailureHandler(
                        authenticationProperties.getFormLogin().getFailureForwardUrl()));
        filter.setSecurityContextRepository(securityContextRepository);

        ObjectProvider<FormQrcodeService> formQrcodeServiceObjectProvider =
                getBeanProvider(applicationContext, FormQrcodeService.class);
        ObjectProvider<FormQrcodeUserDetailsService> formQrcodeUserDetailsServiceObjectProvider =
                getBeanProvider(applicationContext, FormQrcodeUserDetailsService.class);
        com.kuma.boot.security.spring.authentication.login.form.qrcode.FormQrcodeAuthenticationProvider provider =
                new com.kuma.boot.security.spring.authentication.login.form.qrcode.FormQrcodeAuthenticationProvider(
                        this.formQrcodeService == null
                                ? formQrcodeServiceObjectProvider.getIfAvailable(
                                DefaultFormQrcodeService::new)
                                : this.formQrcodeService,
                        this.formQrcodeUserDetailsService == null
                                ? formQrcodeUserDetailsServiceObjectProvider.getIfAvailable(
                                DefaultFormQrcodeUserDetailsService::new)
                                : this.formQrcodeUserDetailsService);

        httpSecurity
                .authenticationProvider(provider)
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }

    public FormQrcodeLoginHttpConfigurer<H> formQrcodeService(FormQrcodeService formQrcodeService) {
        this.formQrcodeService = formQrcodeService;
        return this;
    }

    public FormQrcodeLoginHttpConfigurer<H> formQrcodeUserDetailsService(
            FormQrcodeUserDetailsService formQrcodeUserDetailsService) {
        this.formQrcodeUserDetailsService = formQrcodeUserDetailsService;
        return this;
    }
}
