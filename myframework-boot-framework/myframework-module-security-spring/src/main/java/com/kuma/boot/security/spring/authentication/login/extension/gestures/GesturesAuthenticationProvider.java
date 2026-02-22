/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.MessageSource
 *  org.springframework.context.MessageSourceAware
 *  org.springframework.context.support.MessageSourceAccessor
 *  org.springframework.security.authentication.AuthenticationProvider
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.core.SpringSecurityMessageSource
 *  org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper
 *  org.springframework.security.core.authority.mapping.NullAuthoritiesMapper
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.extension.gestures;

import com.kuma.boot.security.spring.authentication.login.extension.gestures.service.GesturesUserDetailsService;
import java.util.Collection;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

public class GesturesAuthenticationProvider
implements AuthenticationProvider,
InitializingBean,
MessageSourceAware {
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private final GesturesUserDetailsService gesturesUserDetailsService;
    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    public GesturesAuthenticationProvider(GesturesUserDetailsService gesturesUserDetailsService) {
        this.gesturesUserDetailsService = gesturesUserDetailsService;
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(GesturesAuthenticationToken.class, (Object)authentication, () -> this.messages.getMessage("AccountVerificationAuthenticationProvider.onlySupports", "Only AccountVerificationAuthenticationProvider is supported"));
        GesturesAuthenticationToken unAuthenticationToken = (GesturesAuthenticationToken)authentication;
        String username = unAuthenticationToken.getName();
        String passowrd = (String)unAuthenticationToken.getCredentials();
        UserDetails userDetails = this.gesturesUserDetailsService.loadUserByPhone(username);
        return this.createSuccessAuthentication(authentication, userDetails);
    }

    public boolean supports(Class<?> authentication) {
        return GesturesAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull((Object)this.gesturesUserDetailsService, (String)"gesturesUserDetailsService must not be null");
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    protected Authentication createSuccessAuthentication(Authentication authentication, UserDetails user) {
        Collection authorities = this.authoritiesMapper.mapAuthorities(user.getAuthorities());
        GesturesAuthenticationToken authenticationToken = new GesturesAuthenticationToken(user, null, authorities);
        authenticationToken.setDetails(authentication.getDetails());
        return authenticationToken;
    }
}

