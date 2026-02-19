/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.redisson.api.RedissonClient
 *  org.springframework.context.SmartLifecycle
 */
package com.kuma.boot.cache.redis.delay.listener;

import org.redisson.api.RedissonClient;
import org.springframework.context.SmartLifecycle;

public interface RedissonListenerContainer
extends SmartLifecycle {
    public ContainerProperties getContainerProperties();

    public void setListener(RedissonMessageListener<?> var1);

    public void setRedissonClient(RedissonClient var1);
}

