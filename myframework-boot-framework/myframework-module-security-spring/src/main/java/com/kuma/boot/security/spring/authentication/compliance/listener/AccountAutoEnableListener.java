/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.data.redis.connection.Message
 *  org.springframework.data.redis.listener.KeyExpirationEventMessageListener
 *  org.springframework.data.redis.listener.RedisMessageListenerContainer
 */
package com.kuma.boot.security.spring.authentication.compliance.listener;

import com.kuma.boot.security.spring.authentication.compliance.OAuth2AccountStatusManager;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

public class AccountAutoEnableListener
extends KeyExpirationEventMessageListener {
    private static final Logger log = LoggerFactory.getLogger(AccountAutoEnableListener.class);
    private final OAuth2AccountStatusManager accountStatusManager;

    public AccountAutoEnableListener(RedisMessageListenerContainer listenerContainer, OAuth2AccountStatusManager accountStatusManager) {
        super(listenerContainer);
        this.accountStatusManager = accountStatusManager;
    }

    public void onMessage(Message message, byte[] pattern) {
        String key = new String(message.getBody(), StandardCharsets.UTF_8);
        if (StringUtils.contains((CharSequence)key, (CharSequence)"cache:token:locked:user_details:")) {
            String userId = StringUtils.substringAfterLast((String)key, (String)":");
            log.info(" Parse the user [{}] at expired redis cache key [{}]", (Object)userId, (Object)key);
            if (StringUtils.isNotBlank((CharSequence)userId)) {
                log.debug(" Automatically unlock user account [{}]", (Object)userId);
                this.accountStatusManager.enable(userId);
            }
        }
    }
}

