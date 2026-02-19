/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.cache.redis.delay.listener;

public interface RedissonMessageListener<T> {
    public void onMessage(T var1) throws Exception;
}

