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
package com.kuma.boot.security.spring.authentication.login.form.qrcode;

import com.kuma.boot.security.spring.authentication.login.form.FormLoginWebAuthenticationDetailSource;
import com.kuma.boot.security.spring.authentication.login.form.qrcode.service.DefaultFormQrcodeService;
import com.kuma.boot.security.spring.authentication.login.form.qrcode.service.DefaultFormQrcodeUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.form.qrcode.service.FormQrcodeService;
import com.kuma.boot.security.spring.authentication.login.form.qrcode.service.FormQrcodeUserDetailsService;
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

public class FormQrcodeLoginHttpConfigurer<H extends HttpSecurityBuilder<H>>
extends AbstractHttpConfigurer<FormQrcodeLoginHttpConfigurer<H>, H> {
    private FormQrcodeService formQrcodeService;
    private FormQrcodeUserDetailsService formQrcodeUserDetailsService;

    public void configure(H httpSecurity) throws Exception {
        AuthenticationManager authenticationManager = (AuthenticationManager)httpSecurity.getSharedObject(AuthenticationManager.class);
        DelegatingSecurityContextRepository securityContextRepository = new DelegatingSecurityContextRepository(new SecurityContextRepository[]{new RequestAttributeSecurityContextRepository(), new HttpSessionSecurityContextRepository()});
        ApplicationContext applicationContext = (ApplicationContext)httpSecurity.getSharedObject(ApplicationContext.class);
        ObjectProvider<OAuth2AuthenticationProperties> oAuth2AuthenticationPropertiesObjectProvider = OAuth2AuthorizationUtils.oAuth2AuthenticationProperties(applicationContext);
        OAuth2AuthenticationProperties authenticationProperties = (OAuth2AuthenticationProperties)oAuth2AuthenticationPropertiesObjectProvider.getObject();
        FormQrcodeAuthenticationFilter filter = new FormQrcodeAuthenticationFilter(authenticationManager);
        filter.setAuthenticationDetailsSource(new FormLoginWebAuthenticationDetailSource(authenticationProperties));
        filter.setAuthenticationFailureHandler((AuthenticationFailureHandler)new FormLoginAuthenticationFailureHandler(authenticationProperties.getFormLogin().getFailureForwardUrl()));
        filter.setSecurityContextRepository((SecurityContextRepository)securityContextRepository);
        ObjectProvider<FormQrcodeService> formQrcodeServiceObjectProvider = OAuth2AuthorizationUtils.getBeanProvider(applicationContext, FormQrcodeService.class);
        ObjectProvider<FormQrcodeUserDetailsService> formQrcodeUserDetailsServiceObjectProvider = OAuth2AuthorizationUtils.getBeanProvider(applicationContext, FormQrcodeUserDetailsService.class);
        FormQrcodeAuthenticationProvider provider = new FormQrcodeAuthenticationProvider(this.formQrcodeService == null ? (FormQrcodeService)formQrcodeServiceObjectProvider.getIfAvailable(DefaultFormQrcodeService::new) : this.formQrcodeService, this.formQrcodeUserDetailsService == null ? (FormQrcodeUserDetailsService)formQrcodeUserDetailsServiceObjectProvider.getIfAvailable(DefaultFormQrcodeUserDetailsService::new) : this.formQrcodeUserDetailsService);
        httpSecurity.authenticationProvider((AuthenticationProvider)provider).addFilterBefore((Filter)filter, UsernamePasswordAuthenticationFilter.class);
    }

    public FormQrcodeLoginHttpConfigurer<H> formQrcodeService(FormQrcodeService formQrcodeService) {
        this.formQrcodeService = formQrcodeService;
        return this;
    }

    public FormQrcodeLoginHttpConfigurer<H> formQrcodeUserDetailsService(FormQrcodeUserDetailsService formQrcodeUserDetailsService) {
        this.formQrcodeUserDetailsService = formQrcodeUserDetailsService;
        return this;
    }
}

