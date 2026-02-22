/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.servlet.Filter
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.context.ApplicationContext
 *  org.springframework.security.authentication.AuthenticationManager
 *  org.springframework.security.authentication.AuthenticationProvider
 *  org.springframework.security.config.annotation.web.HttpSecurityBuilder
 *  org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
 *  org.springframework.security.web.authentication.AuthenticationFailureHandler
 *  org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
 *  org.springframework.security.web.context.DelegatingSecurityContextRepository
 *  org.springframework.security.web.context.HttpSessionSecurityContextRepository
 *  org.springframework.security.web.context.RequestAttributeSecurityContextRepository
 *  org.springframework.security.web.context.SecurityContextRepository
 */
package com.kuma.boot.security.spring.authentication.login.form.sms;

import com.kuma.boot.security.spring.authentication.login.form.FormLoginWebAuthenticationDetailSource;
import com.kuma.boot.security.spring.authentication.login.form.sms.service.DefaultFormSmsService;
import com.kuma.boot.security.spring.authentication.login.form.sms.service.DefaultFormSmsUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.form.sms.service.FormSmsService;
import com.kuma.boot.security.spring.authentication.login.form.sms.service.FormSmsUserDetailsService;
import com.kuma.boot.security.spring.authentication.response.failure.FormLoginAuthenticationFailureHandler;
import com.kuma.boot.security.spring.properties.OAuth2AuthenticationProperties;
import com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

public class FormSmsLoginHttpConfigurer<H extends HttpSecurityBuilder<H>>
extends AbstractHttpConfigurer<FormSmsLoginHttpConfigurer<H>, H> {
    private FormSmsUserDetailsService formSmsUserDetailsService;
    private FormSmsService formSmsService;

    public void configure(H httpSecurity) throws Exception {
        AuthenticationManager authenticationManager = (AuthenticationManager)httpSecurity.getSharedObject(AuthenticationManager.class);
        DelegatingSecurityContextRepository securityContextRepository = new DelegatingSecurityContextRepository(new SecurityContextRepository[]{new RequestAttributeSecurityContextRepository(), new HttpSessionSecurityContextRepository()});
        ApplicationContext applicationContext = (ApplicationContext)httpSecurity.getSharedObject(ApplicationContext.class);
        ObjectProvider<OAuth2AuthenticationProperties> oAuth2AuthenticationPropertiesObjectProvider = OAuth2AuthorizationUtils.oAuth2AuthenticationProperties(applicationContext);
        OAuth2AuthenticationProperties oAuth2AuthenticationProperties = (OAuth2AuthenticationProperties)oAuth2AuthenticationPropertiesObjectProvider.getObject();
        FormSmsLoginAuthenticationFilter filter = new FormSmsLoginAuthenticationFilter(authenticationManager);
        filter.setAuthenticationDetailsSource(new FormLoginWebAuthenticationDetailSource(oAuth2AuthenticationProperties));
        filter.setAuthenticationFailureHandler((AuthenticationFailureHandler)new FormLoginAuthenticationFailureHandler(oAuth2AuthenticationProperties.getFormLogin().getFailureForwardUrl()));
        filter.setSecurityContextRepository((SecurityContextRepository)securityContextRepository);
        ObjectProvider<FormSmsUserDetailsService> formSmsUserDetailsServiceObjectProvider = OAuth2AuthorizationUtils.getBeanProvider(applicationContext, FormSmsUserDetailsService.class);
        ObjectProvider<FormSmsService> formSmsServiceObjectProvider = OAuth2AuthorizationUtils.getBeanProvider(applicationContext, FormSmsService.class);
        FormSmsLoginAuthenticationProvider provider = new FormSmsLoginAuthenticationProvider(this.formSmsUserDetailsService == null ? (FormSmsUserDetailsService)formSmsUserDetailsServiceObjectProvider.getIfAvailable(DefaultFormSmsUserDetailsService::new) : this.formSmsUserDetailsService, this.formSmsService == null ? (FormSmsService)formSmsServiceObjectProvider.getIfAvailable(DefaultFormSmsService::new) : this.formSmsService);
        httpSecurity.authenticationProvider((AuthenticationProvider)provider).addFilterBefore((Filter)filter, UsernamePasswordAuthenticationFilter.class);
    }

    public FormSmsLoginHttpConfigurer<H> formSmsUserDetailsService(FormSmsUserDetailsService formSmsUserDetailsService) {
        this.formSmsUserDetailsService = formSmsUserDetailsService;
        return this;
    }

    public FormSmsLoginHttpConfigurer<H> formSmsService(FormSmsService formSmsService) {
        this.formSmsService = formSmsService;
        return this;
    }
}

