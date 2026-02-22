/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.annotation.PostConstruct
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.data.redis.listener.RedisMessageListenerContainer
 *  org.springframework.security.core.userdetails.UserDetailsService
 */
package com.kuma.boot.security.spring.configuration;

import com.kuma.boot.security.spring.authentication.compliance.OAuth2AccountStatusManager;
import com.kuma.boot.security.spring.authentication.compliance.listener.AccountAutoEnableListener;
import com.kuma.boot.security.spring.authentication.compliance.processor.changer.AccountStatusChanger;
import com.kuma.boot.security.spring.authentication.compliance.processor.changer.TtcAccountStatusChanger;
import com.kuma.boot.security.spring.authentication.listener.AuthenticationFailureHandler;
import com.kuma.boot.security.spring.authentication.listener.AuthenticationFailureListener;
import com.kuma.boot.security.spring.authentication.stamp.LockedUserDetailsStampManager;
import com.kuma.boot.security.spring.authentication.stamp.SignInFailureLimitedStampManager;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration(proxyBeanMethods=false)
public class OAuth2ComplianceConfiguration {
    private static final Logger log = LoggerFactory.getLogger(OAuth2ComplianceConfiguration.class);

    @PostConstruct
    public void postConstruct() {
        log.info("SDK [OAuth2 Compliance] Auto Configure.");
    }

    @Bean
    public AccountStatusChanger accountStatusChanger() {
        TtcAccountStatusChanger ttcAccountStatusChanger = new TtcAccountStatusChanger();
        log.info("Bean [Account Status Changer] Auto Configure.");
        return ttcAccountStatusChanger;
    }

    @Bean
    public OAuth2AccountStatusManager accountStatusManager(UserDetailsService userDetailsService, AccountStatusChanger accountStatusChanger, LockedUserDetailsStampManager lockedUserDetailsStampManager) {
        OAuth2AccountStatusManager manager = new OAuth2AccountStatusManager(userDetailsService, accountStatusChanger, lockedUserDetailsStampManager);
        log.info("Bean [OAuth2 Account Status Manager] Auto Configure.");
        return manager;
    }

    @Bean
    public AccountAutoEnableListener accountLockStatusListener(RedisMessageListenerContainer redisMessageListenerContainer, OAuth2AccountStatusManager accountStatusManager) {
        AccountAutoEnableListener listener = new AccountAutoEnableListener(redisMessageListenerContainer, accountStatusManager);
        log.info("Bean [OAuth2 Account Lock Status Listener] Auto Configure.");
        return listener;
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationFailureListener authenticationFailureListener(SignInFailureLimitedStampManager stampManager, OAuth2AccountStatusManager accountLockService, AuthenticationFailureHandler authenticationFailureHandler) {
        AuthenticationFailureListener listener = new AuthenticationFailureListener(stampManager, accountLockService, authenticationFailureHandler);
        log.info("Bean [OAuth2 Authentication Failure Listener] Auto Configure.");
        return listener;
    }
}

