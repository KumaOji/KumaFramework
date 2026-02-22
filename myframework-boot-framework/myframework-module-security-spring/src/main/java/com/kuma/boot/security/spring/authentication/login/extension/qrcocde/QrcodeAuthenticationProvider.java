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
package com.kuma.boot.security.spring.authentication.login.extension.qrcocde;

import com.kuma.boot.security.spring.authentication.login.extension.qrcocde.service.QrcodeService;
import com.kuma.boot.security.spring.authentication.login.extension.qrcocde.service.QrcodeUserDetailsService;
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

public class QrcodeAuthenticationProvider
implements AuthenticationProvider,
InitializingBean,
MessageSourceAware {
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private final QrcodeUserDetailsService qrcodeUserDetailsService;
    private final QrcodeService qrcodeService;
    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    public QrcodeAuthenticationProvider(QrcodeService qrcodeService, QrcodeUserDetailsService qrcodeUserDetailsService) {
        this.qrcodeService = qrcodeService;
        this.qrcodeUserDetailsService = qrcodeUserDetailsService;
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(QrcodeAuthenticationToken.class, (Object)authentication, () -> this.messages.getMessage("AccountVerificationAuthenticationProvider.onlySupports", "Only AccountVerificationAuthenticationProvider is supported"));
        QrcodeAuthenticationToken unAuthenticationToken = (QrcodeAuthenticationToken)authentication;
        String username = unAuthenticationToken.getName();
        String passowrd = (String)unAuthenticationToken.getCredentials();
        this.qrcodeService.verifyQrcode("qrocde");
        UserDetails userDetails = this.qrcodeUserDetailsService.loadUserByPhone(username);
        return this.createSuccessAuthentication(authentication, userDetails);
    }

    public boolean supports(Class<?> authentication) {
        return QrcodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull((Object)this.qrcodeUserDetailsService, (String)"qrcodeUserDetailsService must not be null");
        Assert.notNull((Object)this.qrcodeService, (String)"qrcodeService must not be null");
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    protected Authentication createSuccessAuthentication(Authentication authentication, UserDetails user) {
        Collection authorities = this.authoritiesMapper.mapAuthorities(user.getAuthorities());
        QrcodeAuthenticationToken authenticationToken = new QrcodeAuthenticationToken(user, null, authorities);
        authenticationToken.setDetails(authentication.getDetails());
        return authenticationToken;
    }
}

