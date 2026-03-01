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

package com.kuma.boot.security.spring.authentication.login.form.sms;

import static com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils.getBeanProvider;
import static com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils.oAuth2AuthenticationProperties;

import com.kuma.boot.security.spring.authentication.login.form.FormLoginWebAuthenticationDetailSource;
import com.kuma.boot.security.spring.authentication.login.form.sms.service.DefaultFormSmsService;
import com.kuma.boot.security.spring.authentication.login.form.sms.service.DefaultFormSmsUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.form.sms.service.FormSmsService;
import com.kuma.boot.security.spring.authentication.login.form.sms.service.FormSmsUserDetailsService;
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
 * <p>OAuth2 Form Login Configurer </p>
 * <p>
 * 使用此种方式，相当于额外增加了一种表单登录方式。因此对原有的 http.formlogin进行的配置，对当前此种方式的配置并不生效。
 *
 * @author kuma
 * @version 2023.04
 * @since 2023-06-29 16:38:04
 */
public class FormSmsLoginHttpConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractHttpConfigurer<FormSmsLoginHttpConfigurer<H>, H> {

    private FormSmsUserDetailsService formSmsUserDetailsService;
    private FormSmsService formSmsService;

    @Override
    public void configure(H httpSecurity) {
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
        OAuth2AuthenticationProperties oAuth2AuthenticationProperties =
                oAuth2AuthenticationPropertiesObjectProvider.getObject();

        com.kuma.boot.security.spring.authentication.login.form.sms.FormSmsLoginAuthenticationFilter filter =
                new com.kuma.boot.security.spring.authentication.login.form.sms.FormSmsLoginAuthenticationFilter(authenticationManager);
        filter.setAuthenticationDetailsSource(
                new FormLoginWebAuthenticationDetailSource(oAuth2AuthenticationProperties));

        filter.setAuthenticationFailureHandler(
                new FormLoginAuthenticationFailureHandler(
                        oAuth2AuthenticationProperties.getFormLogin().getFailureForwardUrl()));
        filter.setSecurityContextRepository(securityContextRepository);

        ObjectProvider<FormSmsUserDetailsService> formSmsUserDetailsServiceObjectProvider =
                getBeanProvider(applicationContext, FormSmsUserDetailsService.class);
        ObjectProvider<FormSmsService> formSmsServiceObjectProvider =
                getBeanProvider(applicationContext, FormSmsService.class);

        com.kuma.boot.security.spring.authentication.login.form.sms.FormSmsLoginAuthenticationProvider provider =
                new com.kuma.boot.security.spring.authentication.login.form.sms.FormSmsLoginAuthenticationProvider(
                        this.formSmsUserDetailsService == null
                                ? formSmsUserDetailsServiceObjectProvider.getIfAvailable(
                                DefaultFormSmsUserDetailsService::new)
                                : this.formSmsUserDetailsService,
                        this.formSmsService == null
                                ? formSmsServiceObjectProvider.getIfAvailable(
                                DefaultFormSmsService::new)
                                : this.formSmsService);

        httpSecurity
                .authenticationProvider(provider)
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }

    public FormSmsLoginHttpConfigurer<H> formSmsUserDetailsService(
            FormSmsUserDetailsService formSmsUserDetailsService) {
        this.formSmsUserDetailsService = formSmsUserDetailsService;
        return this;
    }

    public FormSmsLoginHttpConfigurer<H> formSmsService(FormSmsService formSmsService) {
        this.formSmsService = formSmsService;
        return this;
    }
}
