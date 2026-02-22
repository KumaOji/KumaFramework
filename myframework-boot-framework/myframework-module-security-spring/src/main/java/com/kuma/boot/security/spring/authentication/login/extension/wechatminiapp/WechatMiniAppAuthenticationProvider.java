/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
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
package com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp;

import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.client.WechatMiniAppRequest;
import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.service.WechatMiniAppSessionKeyCacheService;
import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.service.WechatMiniAppUserDetailsService;
import java.util.Collection;
import java.util.Objects;
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

public class WechatMiniAppAuthenticationProvider
implements AuthenticationProvider,
MessageSourceAware {
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private final WechatMiniAppUserDetailsService wechatMiniAppUserDetailsService;
    private WechatMiniAppSessionKeyCacheService wechatMiniAppSessionKeyCacheService;
    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    public WechatMiniAppAuthenticationProvider(WechatMiniAppUserDetailsService wechatMiniAppUserDetailsService, WechatMiniAppSessionKeyCacheService wechatMiniAppSessionKeyCacheService) {
        this.wechatMiniAppUserDetailsService = wechatMiniAppUserDetailsService;
        this.wechatMiniAppSessionKeyCacheService = wechatMiniAppSessionKeyCacheService;
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(WechatMiniAppAuthenticationToken.class, (Object)authentication, () -> this.messages.getMessage("MiniAppAuthenticationProvider.onlySupports", "Only MiniAppAuthenticationToken is supported"));
        WechatMiniAppAuthenticationToken unAuthenticationToken = (WechatMiniAppAuthenticationToken)authentication;
        WechatMiniAppRequest credentials = (WechatMiniAppRequest)unAuthenticationToken.getCredentials();
        String clientId = credentials.getClientId();
        String openId = credentials.getOpenId();
        UserDetails userDetails = this.wechatMiniAppUserDetailsService.loadByOpenId(clientId, openId);
        if (Objects.isNull(userDetails)) {
            userDetails = this.wechatMiniAppUserDetailsService.register(credentials, this.wechatMiniAppSessionKeyCacheService.get(clientId + "::" + openId));
        }
        return this.createSuccessAuthentication(authentication, userDetails);
    }

    public boolean supports(Class<?> authentication) {
        return WechatMiniAppAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    protected Authentication createSuccessAuthentication(Authentication authentication, UserDetails user) {
        Collection authorities = this.authoritiesMapper.mapAuthorities(user.getAuthorities());
        WechatMiniAppAuthenticationToken authenticationToken = new WechatMiniAppAuthenticationToken(user, authorities);
        authenticationToken.setDetails(authentication.getPrincipal());
        return authenticationToken;
    }
}

