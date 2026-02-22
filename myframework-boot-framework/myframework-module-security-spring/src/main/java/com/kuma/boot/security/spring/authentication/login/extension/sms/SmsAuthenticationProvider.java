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
package com.kuma.boot.security.spring.authentication.login.extension.sms;

import com.kuma.boot.security.spring.authentication.login.extension.sms.service.SmsCheckCodeService;
import com.kuma.boot.security.spring.authentication.login.extension.sms.service.SmsUserDetailsService;
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

public class SmsAuthenticationProvider
implements AuthenticationProvider,
InitializingBean,
MessageSourceAware {
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private final SmsUserDetailsService smsUserDetailsService;
    private final SmsCheckCodeService smsCheckCodeService;
    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    public SmsAuthenticationProvider(SmsUserDetailsService smsUserDetailsService, SmsCheckCodeService smsCheckCodeService) {
        this.smsUserDetailsService = smsUserDetailsService;
        this.smsCheckCodeService = smsCheckCodeService;
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(SmsAuthenticationToken.class, (Object)authentication, () -> this.messages.getMessage("CaptchaAuthenticationProvider.onlySupports", "Only CaptchaAuthenticationToken is supported"));
        SmsAuthenticationToken smsAuthenticationToken = (SmsAuthenticationToken)authentication;
        String phone = smsAuthenticationToken.getName();
        String rawCode = (String)smsAuthenticationToken.getCredentials();
        String type = smsAuthenticationToken.getType();
        if (this.smsCheckCodeService.verifyCaptcha(phone, rawCode)) {
            UserDetails userDetails = this.smsUserDetailsService.loadUserByPhone(phone, type);
            return this.createSuccessAuthentication(authentication, userDetails);
        }
        throw new BadCredentialsException("captcha is not matched");
    }

    public boolean supports(Class<?> authentication) {
        return SmsAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull((Object)this.smsUserDetailsService, (String)"phoneUserDetailsService must not be null");
        Assert.notNull((Object)this.smsCheckCodeService, (String)"phoneService must not be null");
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    protected Authentication createSuccessAuthentication(Authentication authentication, UserDetails user) {
        Collection authorities = this.authoritiesMapper.mapAuthorities(user.getAuthorities());
        String type = "";
        String captcha = "";
        if (authentication instanceof SmsAuthenticationToken) {
            SmsAuthenticationToken accountAuthenticationToken = (SmsAuthenticationToken)authentication;
            type = accountAuthenticationToken.getType();
            captcha = (String)accountAuthenticationToken.getCredentials();
        }
        SmsAuthenticationToken authenticationToken = new SmsAuthenticationToken(user, captcha, type, authorities);
        authenticationToken.setDetails(authentication.getDetails());
        return authenticationToken;
    }
}

