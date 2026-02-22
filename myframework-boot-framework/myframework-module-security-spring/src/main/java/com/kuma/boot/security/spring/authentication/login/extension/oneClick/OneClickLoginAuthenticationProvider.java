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
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.core.userdetails.UsernameNotFoundException
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.extension.oneClick;

import com.kuma.boot.security.spring.authentication.login.extension.oneClick.service.OneClickLoginService;
import com.kuma.boot.security.spring.authentication.login.extension.oneClick.service.OneClickUserDetailsService;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

public class OneClickLoginAuthenticationProvider
implements AuthenticationProvider,
InitializingBean,
MessageSourceAware {
    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private final OneClickUserDetailsService oneClickUserDetailsService;
    private final OneClickLoginService oneClickLoginService;

    public OneClickLoginAuthenticationProvider(OneClickUserDetailsService oneClickUserDetailsService, OneClickLoginService oneClickLoginService) {
        this.oneClickUserDetailsService = oneClickUserDetailsService;
        this.oneClickLoginService = oneClickLoginService;
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Map<String, String> otherParamMap;
        UserDetails user;
        Assert.isInstanceOf(OneClickLoginAuthenticationToken.class, (Object)authentication, () -> this.messages.getMessage("AccountVerificationAuthenticationProvider.onlySupports", "Only AccountVerificationAuthenticationProvider is supported"));
        if (!this.supports(authentication.getClass())) {
            return null;
        }
        OneClickLoginAuthenticationToken authenticationToken = (OneClickLoginAuthenticationToken)authentication;
        if (authentication.isAuthenticated()) {
            return authentication;
        }
        try {
            user = this.oneClickUserDetailsService.loadUserByOneClick((String)authenticationToken.getPrincipal());
        }
        catch (UsernameNotFoundException e) {
            user = null;
        }
        if (user == null) {
            user = this.oneClickUserDetailsService.registerUser((String)authenticationToken.getPrincipal());
        }
        if (Objects.nonNull(otherParamMap = authenticationToken.getOtherParamMap()) && !otherParamMap.isEmpty()) {
            this.oneClickLoginService.otherParamsHandler(user, otherParamMap);
        }
        OneClickLoginAuthenticationToken authenticationResult = new OneClickLoginAuthenticationToken(user, otherParamMap, user.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    public boolean supports(Class<?> authentication) {
        return OneClickLoginAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull((Object)this.oneClickUserDetailsService, (String)"oneClickUserDetailsService must not be null");
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }
}

