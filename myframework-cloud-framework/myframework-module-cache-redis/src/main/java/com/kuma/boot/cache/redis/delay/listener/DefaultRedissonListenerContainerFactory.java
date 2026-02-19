/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.cache.redis.delay.listener;

public class DefaultRedissonListenerContainerFactory
implements RedissonListenerContainerFactory {
    @Override
    public RedissonListenerContainer createListenerContainer(ContainerProperties containerProperties) {
        int concurrency = containerProperties.getConcurrency();
        return new ConcurrentRedissonListenerContainer(containerProperties, Math.max(concurrency, 1));
    }
}

