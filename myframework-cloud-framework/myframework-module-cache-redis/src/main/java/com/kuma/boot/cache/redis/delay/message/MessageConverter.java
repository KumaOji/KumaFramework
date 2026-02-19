/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.cache.redis.delay.message;

import com.kuma.boot.cache.redis.delay.MessageConversionException;

import java.util.Map;

public interface MessageConverter {
    public QueueMessage<?> toMessage(Object var1, Map<String, Object> var2) throws MessageConversionException;

    public Object fromMessage(RedissonMessage var1) throws MessageConversionException;
}

