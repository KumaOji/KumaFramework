/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.cache.redis.enums;

public enum CacheType {
    CAFFEINE,
    REDIS,
    JETCACHE;


    public boolean eq(CacheType cacheType) {
        return cacheType != null && this.name().equals(cacheType.name());
    }
}

