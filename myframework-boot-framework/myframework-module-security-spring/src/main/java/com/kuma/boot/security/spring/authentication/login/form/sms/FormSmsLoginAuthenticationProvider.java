/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.MessageSource
 *  org.springframework.context.MessageSourceAware
 *  org.springframework.context.support.MessageSourceAccessor
 *  org.springframework.security.authentication.AuthenticationProvider
 *  org.springframework.security.authentication.BadCredentialsException
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.core.SpringSecurityMessageSource
 *  org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper
 *  org.springframework.security.core.authority.mapping.NullAuthoritiesMapper
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.form.sms;

import com.kuma.boot.security.spring.authentication.login.form.sms.service.FormSmsService;
import com.kuma.boot.security.spring.authentication.login.form.sms.service.FormSmsUserDetailsService;
import java.util.Collection;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

public class FormSmsLoginAuthenticationProvider
implements AuthenticationProvider,
InitializingBean,
MessageSourceAware {
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private final FormSmsUserDetailsService formSmsUserDetailsService;
    private final FormSmsService formSmsService;
    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    public FormSmsLoginAuthenticationProvider(FormSmsUserDetailsService formSmsUserDetailsService, FormSmsService formSmsService) {
        this.formSmsUserDetailsService = formSmsUserDetailsService;
        this.formSmsService = formSmsService;
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(FormSmsLoginAuthenticationToken.class, (Object)authentication, () -> this.messages.getMessage("CaptchaAuthenticationProvider.onlySupports", "Only CaptchaAuthenticationToken is supported"));
        FormSmsLoginAuthenticationToken unAuthenticationToken = (FormSmsLoginAuthenticationToken)authentication;
        String phone = unAuthenticationToken.getName();
        String rawCode = (String)unAuthenticationToken.getCredentials();
        String type = unAuthenticationToken.getType();
        if (this.formSmsService.verifyCaptcha(phone, rawCode)) {
            UserDetails userDetails = this.formSmsUserDetailsService.loadUserByPhone(phone, type);
            return this.createSuccessAuthentication(authentication, userDetails);
        }
        throw new BadCredentialsException("captcha is not matched");
    }

    public boolean supports(Class<?> authentication) {
        return FormSmsLoginAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull((Object)this.formSmsUserDetailsService, (String)"phoneUserDetailsService must not be null");
        Assert.notNull((Object)this.formSmsService, (String)"phoneService must not be null");
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    protected Authentication createSuccessAuthentication(Authentication authentication, UserDetails user) {
        Collection authorities = this.authoritiesMapper.mapAuthorities(user.getAuthorities());
        String type = "";
        String captcha = "";
        if (authentication instanceof FormSmsLoginAuthenticationToken) {
            FormSmsLoginAuthenticationToken accountAuthenticationToken = (FormSmsLoginAuthenticationToken)authentication;
            type = accountAuthenticationToken.getType();
            captcha = (String)accountAuthenticationToken.getCredentials();
        }
        FormSmsLoginAuthenticationToken authenticationToken = new FormSmsLoginAuthenticationToken(user, captcha, type, authorities);
        authenticationToken.setDetails(authentication.getDetails());
        return authenticationToken;
    }
}

