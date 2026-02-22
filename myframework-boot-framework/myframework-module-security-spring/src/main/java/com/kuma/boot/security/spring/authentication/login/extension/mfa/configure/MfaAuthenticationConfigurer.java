/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.Filter
 *  org.springframework.context.ApplicationContext
 *  org.springframework.http.HttpMethod
 *  org.springframework.security.authentication.AuthenticationManager
 *  org.springframework.security.authentication.AuthenticationProvider
 *  org.springframework.security.config.annotation.SecurityBuilder
 *  org.springframework.security.config.annotation.web.builders.HttpSecurity
 *  org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
 *  org.springframework.security.core.userdetails.UserDetailsService
 *  org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
 *  org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
 *  org.springframework.security.web.util.matcher.RequestMatcher
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.extension.mfa.configure;

import com.kuma.boot.security.spring.authentication.login.extension.mfa.MfaAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.authentication.MfaAuthenticationProvider;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.totp.DefaultTotpManager;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.totp.MfaAuthenticationManager;
import jakarta.servlet.Filter;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

public class MfaAuthenticationConfigurer
extends AbstractHttpConfigurer<MfaAuthenticationConfigurer, HttpSecurity> {
    private MfaAuthenticationManager mfaAuthenticationManager;

    public MfaAuthenticationConfigurer mfaAuthenticationManager(MfaAuthenticationManager mfaAuthenticationManager) {
        Assert.notNull((Object)mfaAuthenticationManager, (String)"mfaAuthenticationManager can not be null");
        this.mfaAuthenticationManager = mfaAuthenticationManager;
        return this;
    }

    public void init(HttpSecurity http) throws Exception {
        if (this.mfaAuthenticationManager == null) {
            this.mfaAuthenticationManager = new DefaultTotpManager();
        }
        ApplicationContext applicationContext = (ApplicationContext)http.getSharedObject(ApplicationContext.class);
        UserDetailsService userDetailsService = (UserDetailsService)applicationContext.getBean(UserDetailsService.class);
        http.authenticationProvider((AuthenticationProvider)this.postProcess(new MfaAuthenticationProvider(userDetailsService, this.mfaAuthenticationManager)));
        super.init((SecurityBuilder)http);
    }

    public void configure(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = (AuthenticationManager)http.getSharedObject(AuthenticationManager.class);
        MfaAuthenticationFilter mfaAuthenticationFilter = new MfaAuthenticationFilter(authenticationManager, (RequestMatcher)PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login"));
        http.addFilterBefore((Filter)this.postProcess((Object)mfaAuthenticationFilter), UsernamePasswordAuthenticationFilter.class);
        super.configure((SecurityBuilder)http);
    }
}

