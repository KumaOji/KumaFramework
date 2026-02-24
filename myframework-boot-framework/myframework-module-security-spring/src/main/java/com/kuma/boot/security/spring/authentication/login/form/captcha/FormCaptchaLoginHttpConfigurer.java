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

package com.kuma.boot.security.spring.authentication.login.form.captcha;

import static com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils.getBeanProvider;
import static com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils.oAuth2AuthenticationProperties;

import com.kuma.boot.security.spring.authentication.login.form.FormLoginWebAuthenticationDetailSource;
import com.kuma.boot.security.spring.authentication.login.form.captcha.service.DefaultFormCaptchaCheckService;
import com.kuma.boot.security.spring.authentication.login.form.captcha.service.DefaultFormCaptchaUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.form.captcha.service.FormCaptchaCheckService;
import com.kuma.boot.security.spring.authentication.login.form.captcha.service.FormCaptchaUserDetailsService;
import com.kuma.boot.security.spring.authentication.response.failure.FormLoginAuthenticationFailureHandler;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2AuthenticationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
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
public class FormCaptchaLoginHttpConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractHttpConfigurer<FormCaptchaLoginHttpConfigurer<H>, H> {

    private static final Logger log = LoggerFactory.getLogger(FormCaptchaLoginHttpConfigurer.class);

    private FormCaptchaCheckService formCaptchaCheckService;
    private FormCaptchaUserDetailsService formCaptchaUserDetailsService;
    private UserDetailsService userDetailsService;

    @Override
    public void configure(H httpSecurity) {
        AuthenticationManager authenticationManager =
                httpSecurity.getSharedObject(AuthenticationManager.class);
        // SecurityContextRepository securityContextRepository =
        //	httpSecurity.getSharedObject(SecurityContextRepository.class);

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

        com.kuma.boot.security.spring.authentication.login.form.captcha.FormCaptchaLoginAuthenticationFilter filter =
                new com.kuma.boot.security.spring.authentication.login.form.captcha.FormCaptchaLoginAuthenticationFilter(authenticationManager);
        filter.setUsernameParameter(
                oAuth2AuthenticationProperties.getFormLogin().getUsernameParameter());
        filter.setPasswordParameter(
                oAuth2AuthenticationProperties.getFormLogin().getPasswordParameter());
        filter.setAuthenticationDetailsSource(
                new FormLoginWebAuthenticationDetailSource(oAuth2AuthenticationProperties));

        filter.setAuthenticationFailureHandler(
                new FormLoginAuthenticationFailureHandler(
                        oAuth2AuthenticationProperties.getFormLogin().getFailureForwardUrl()));
        filter.setSecurityContextRepository(securityContextRepository);

        ObjectProvider<FormCaptchaCheckService> formCaptchaCheckServiceObjectProvider =
                getBeanProvider(applicationContext, FormCaptchaCheckService.class);
        ObjectProvider<FormCaptchaUserDetailsService> formCaptchaUserDetailsServiceObjectProvider =
                getBeanProvider(applicationContext, FormCaptchaUserDetailsService.class);
        ObjectProvider<UserDetailsService> userDetailsServiceObjectProvider =
                getBeanProvider(applicationContext, UserDetailsService.class);

        com.kuma.boot.security.spring.authentication.login.form.captcha.FormCaptchaLoginAuthenticationProvider provider =
                new com.kuma.boot.security.spring.authentication.login.form.captcha.FormCaptchaLoginAuthenticationProvider(
                        userDetailsService,
                        this.formCaptchaCheckService == null
                                ? formCaptchaCheckServiceObjectProvider.getIfAvailable(
                                DefaultFormCaptchaCheckService::new)
                                : this.formCaptchaCheckService,
                        this.formCaptchaUserDetailsService == null
                                ? formCaptchaUserDetailsServiceObjectProvider.getIfAvailable(
                                DefaultFormCaptchaUserDetailsService::new)
                                : this.formCaptchaUserDetailsService);
//        provider.setUserDetailsService(
//                this.userDetailsService == null
//                        ? userDetailsServiceObjectProvider.getObject()
//                        : this.userDetailsService);
        provider.setHideUserNotFoundExceptions(false);

        httpSecurity
                .authenticationProvider(provider)
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }

    public FormCaptchaLoginHttpConfigurer<H> formCaptchaCheckService(
            FormCaptchaCheckService formCaptchaCheckService) {
        this.formCaptchaCheckService = formCaptchaCheckService;
        return this;
    }

    public FormCaptchaLoginHttpConfigurer<H> formCaptchaUserDetailsService(
            FormCaptchaUserDetailsService formCaptchaUserDetailsService) {
        this.formCaptchaUserDetailsService = formCaptchaUserDetailsService;
        return this;
    }

    public FormCaptchaLoginHttpConfigurer<H> formUserDetailsService(
            UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        return this;
    }
}
