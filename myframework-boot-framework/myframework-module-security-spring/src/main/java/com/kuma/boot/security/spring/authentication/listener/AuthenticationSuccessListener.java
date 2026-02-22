/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.ApplicationListener
 *  org.springframework.security.authentication.event.AuthenticationSuccessEvent
 *  org.springframework.security.core.Authentication
 */
package com.kuma.boot.security.spring.authentication.listener;

import com.kuma.boot.security.spring.authentication.compliance.service.ComplianceService;
import com.kuma.boot.security.spring.authentication.stamp.SignInFailureLimitedStampManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

public class AuthenticationSuccessListener
implements ApplicationListener<AuthenticationSuccessEvent> {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationSuccessListener.class);
    private final SignInFailureLimitedStampManager stampManager;
    private final ComplianceService complianceService;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    public AuthenticationSuccessListener(SignInFailureLimitedStampManager stampManager, ComplianceService complianceService, AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.stampManager = stampManager;
        this.complianceService = complianceService;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        log.debug(" Authentication Success Listener!");
        Authentication authentication = event.getAuthentication();
        this.authenticationSuccessHandler.handle(event);
    }
}

