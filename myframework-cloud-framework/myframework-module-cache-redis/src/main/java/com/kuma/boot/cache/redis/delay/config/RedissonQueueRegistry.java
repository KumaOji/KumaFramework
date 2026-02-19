/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.cache.redis.delay.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedissonQueueRegistry {
    private final Map<String, QueueRegistryInfo> registryInfoContainer = new ConcurrentHashMap<String, QueueRegistryInfo>(8);

    protected void registerQueueInfo(String queueName, QueueRegistryInfo queueInfo) {
        if (queueInfo == null) {
            return;
        }
        this.registryInfoContainer.put(queueName, queueInfo);
    }

    public QueueRegistryInfo getRegistryInfo(String queueName) {
        return this.registryInfoContainer.get(queueName);
    }
}

