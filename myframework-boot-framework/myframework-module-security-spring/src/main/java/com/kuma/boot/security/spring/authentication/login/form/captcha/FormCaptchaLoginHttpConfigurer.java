/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.servlet.Filter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.context.ApplicationContext
 *  org.springframework.security.authentication.AuthenticationManager
 *  org.springframework.security.authentication.AuthenticationProvider
 *  org.springframework.security.config.annotation.web.HttpSecurityBuilder
 *  org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
 *  org.springframework.security.core.userdetails.UserDetailsService
 *  org.springframework.security.web.authentication.AuthenticationFailureHandler
 *  org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
 *  org.springframework.security.web.context.DelegatingSecurityContextRepository
 *  org.springframework.security.web.context.HttpSessionSecurityContextRepository
 *  org.springframework.security.web.context.RequestAttributeSecurityContextRepository
 *  org.springframework.security.web.context.SecurityContextRepository
 */
package com.kuma.boot.security.spring.authentication.login.form.captcha;

import com.kuma.boot.security.spring.authentication.login.form.FormLoginWebAuthenticationDetailSource;
import com.kuma.boot.security.spring.authentication.login.form.captcha.service.DefaultFormCaptchaCheckService;
import com.kuma.boot.security.spring.authentication.login.form.captcha.service.DefaultFormCaptchaUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.form.captcha.service.FormCaptchaCheckService;
import com.kuma.boot.security.spring.authentication.login.form.captcha.service.FormCaptchaUserDetailsService;
import com.kuma.boot.security.spring.authentication.response.failure.FormLoginAuthenticationFailureHandler;
import com.kuma.boot.security.spring.properties.OAuth2AuthenticationProperties;
import com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils;
import jakarta.servlet.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

public class FormCaptchaLoginHttpConfigurer<H extends HttpSecurityBuilder<H>>
extends AbstractHttpConfigurer<FormCaptchaLoginHttpConfigurer<H>, H> {
    private static final Logger log = LoggerFactory.getLogger(FormCaptchaLoginHttpConfigurer.class);
    private FormCaptchaCheckService formCaptchaCheckService;
    private FormCaptchaUserDetailsService formCaptchaUserDetailsService;
    private UserDetailsService userDetailsService;

    public void configure(H httpSecurity) throws Exception {
        AuthenticationManager authenticationManager = (AuthenticationManager)httpSecurity.getSharedObject(AuthenticationManager.class);
        DelegatingSecurityContextRepository securityContextRepository = new DelegatingSecurityContextRepository(new SecurityContextRepository[]{new RequestAttributeSecurityContextRepository(), new HttpSessionSecurityContextRepository()});
        ApplicationContext applicationContext = (ApplicationContext)httpSecurity.getSharedObject(ApplicationContext.class);
        ObjectProvider<OAuth2AuthenticationProperties> oAuth2AuthenticationPropertiesObjectProvider = OAuth2AuthorizationUtils.oAuth2AuthenticationProperties(applicationContext);
        OAuth2AuthenticationProperties oAuth2AuthenticationProperties = (OAuth2AuthenticationProperties)oAuth2AuthenticationPropertiesObjectProvider.getObject();
        FormCaptchaLoginAuthenticationFilter filter = new FormCaptchaLoginAuthenticationFilter(authenticationManager);
        filter.setUsernameParameter(oAuth2AuthenticationProperties.getFormLogin().getUsernameParameter());
        filter.setPasswordParameter(oAuth2AuthenticationProperties.getFormLogin().getPasswordParameter());
        filter.setAuthenticationDetailsSource(new FormLoginWebAuthenticationDetailSource(oAuth2AuthenticationProperties));
        filter.setAuthenticationFailureHandler((AuthenticationFailureHandler)new FormLoginAuthenticationFailureHandler(oAuth2AuthenticationProperties.getFormLogin().getFailureForwardUrl()));
        filter.setSecurityContextRepository((SecurityContextRepository)securityContextRepository);
        ObjectProvider<FormCaptchaCheckService> formCaptchaCheckServiceObjectProvider = OAuth2AuthorizationUtils.getBeanProvider(applicationContext, FormCaptchaCheckService.class);
        ObjectProvider<FormCaptchaUserDetailsService> formCaptchaUserDetailsServiceObjectProvider = OAuth2AuthorizationUtils.getBeanProvider(applicationContext, FormCaptchaUserDetailsService.class);
        ObjectProvider<UserDetailsService> userDetailsServiceObjectProvider = OAuth2AuthorizationUtils.getBeanProvider(applicationContext, UserDetailsService.class);
        FormCaptchaLoginAuthenticationProvider provider = new FormCaptchaLoginAuthenticationProvider(this.formCaptchaCheckService == null ? (FormCaptchaCheckService)formCaptchaCheckServiceObjectProvider.getIfAvailable(DefaultFormCaptchaCheckService::new) : this.formCaptchaCheckService, this.formCaptchaUserDetailsService == null ? (FormCaptchaUserDetailsService)formCaptchaUserDetailsServiceObjectProvider.getIfAvailable(DefaultFormCaptchaUserDetailsService::new) : this.formCaptchaUserDetailsService);
        provider.setUserDetailsService(this.userDetailsService == null ? (UserDetailsService)userDetailsServiceObjectProvider.getObject() : this.userDetailsService);
        provider.setHideUserNotFoundExceptions(false);
        httpSecurity.authenticationProvider((AuthenticationProvider)provider).addFilterBefore((Filter)filter, UsernamePasswordAuthenticationFilter.class);
    }

    public FormCaptchaLoginHttpConfigurer<H> formCaptchaCheckService(FormCaptchaCheckService formCaptchaCheckService) {
        this.formCaptchaCheckService = formCaptchaCheckService;
        return this;
    }

    public FormCaptchaLoginHttpConfigurer<H> formCaptchaUserDetailsService(FormCaptchaUserDetailsService formCaptchaUserDetailsService) {
        this.formCaptchaUserDetailsService = formCaptchaUserDetailsService;
        return this;
    }

    public FormCaptchaLoginHttpConfigurer<H> formUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        return this;
    }
}

