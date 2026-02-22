/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.apache.commons.collections4.MapUtils
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.ApplicationListener
 *  org.springframework.security.authentication.UsernamePasswordAuthenticationToken
 *  org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent
 *  org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent
 *  org.springframework.security.core.Authentication
 */
package com.kuma.boot.security.spring.authentication.listener;

import com.kuma.boot.security.spring.authentication.compliance.OAuth2AccountStatusManager;
import com.kuma.boot.security.spring.authentication.stamp.SignInFailureLimitedStampManager;
import com.kuma.boot.security.spring.exception.MaximumLimitExceededException;
import java.time.Duration;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;

public class AuthenticationFailureListener
implements ApplicationListener<AbstractAuthenticationFailureEvent> {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationFailureListener.class);
    private final SignInFailureLimitedStampManager stampManager;
    private final OAuth2AccountStatusManager accountStatusManager;
    private final AuthenticationFailureHandler authenticationFailureHandler;

    public AuthenticationFailureListener(SignInFailureLimitedStampManager stampManager, OAuth2AccountStatusManager accountStatusManager, AuthenticationFailureHandler authenticationFailureHandler) {
        this.stampManager = stampManager;
        this.accountStatusManager = accountStatusManager;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    public void onApplicationEvent(AbstractAuthenticationFailureEvent event) {
        log.debug(" User sign in catch failure event : [{}].", (Object)event.getClass().getName());
        if (event instanceof AuthenticationFailureBadCredentialsEvent) {
            Authentication authentication = event.getAuthentication();
            String username = null;
            username = this.authenticationFailureHandler.handle(event);
            if (authentication instanceof UsernamePasswordAuthenticationToken) {
                log.debug(" Toke object in failure event  is UsernamePasswordAuthenticationToken");
                UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken)authentication;
                Object principal = token.getPrincipal();
                if (principal instanceof String) {
                    username = (String)principal;
                }
            }
            if (StringUtils.isNotBlank((CharSequence)username)) {
                log.debug(" Parse the username in failure event is [{}].", (Object)username);
                int maxTimes = this.stampManager.getAuthenticationProperties().getSignInFailureLimited().getMaxTimes();
                Duration expire = this.stampManager.getAuthenticationProperties().getSignInFailureLimited().getExpire();
                try {
                    int times = this.stampManager.counting(username, maxTimes, expire, true, "AuthenticationFailureListener");
                    log.debug(" Sign in user input password error [{}] items", (Object)times);
                }
                catch (MaximumLimitExceededException e) {
                    log.warn(" User [{}] password error [{}] items, LOCK ACCOUNT!", (Object)username, (Object)maxTimes);
                    this.accountStatusManager.lock(username);
                }
            }
        }
    }

    private String getPrincipal(Map<String, Object> params) {
        Object value;
        if (MapUtils.isNotEmpty(params) && params.containsKey("username") && ObjectUtils.isNotEmpty((Object)(value = params.get("username")))) {
            return (String)value;
        }
        return null;
    }
}

