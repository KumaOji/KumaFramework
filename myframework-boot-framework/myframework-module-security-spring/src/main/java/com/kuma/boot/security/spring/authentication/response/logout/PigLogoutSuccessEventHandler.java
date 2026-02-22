/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationListener
 *  org.springframework.security.authentication.event.LogoutSuccessEvent
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
 */
package com.kuma.boot.security.spring.authentication.response.logout;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class PigLogoutSuccessEventHandler
implements ApplicationListener<LogoutSuccessEvent> {
    public void onApplicationEvent(LogoutSuccessEvent event) {
        Authentication authentication = (Authentication)event.getSource();
        if (authentication instanceof PreAuthenticatedAuthenticationToken) {
            this.handle(authentication);
        }
    }

    public void handle(Authentication authentication) {
    }
}

