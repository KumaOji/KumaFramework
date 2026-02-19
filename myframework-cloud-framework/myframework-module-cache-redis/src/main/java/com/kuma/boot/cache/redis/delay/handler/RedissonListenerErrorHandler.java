/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.messaging.Message
 */
package com.kuma.boot.cache.redis.delay.handler;

import com.kuma.boot.cache.redis.delay.message.RedissonMessage;
import org.springframework.messaging.Message;

@FunctionalInterface
public interface RedissonListenerErrorHandler {
    public void handleError(RedissonMessage var1, Message<?> var2, Throwable var3);
}

