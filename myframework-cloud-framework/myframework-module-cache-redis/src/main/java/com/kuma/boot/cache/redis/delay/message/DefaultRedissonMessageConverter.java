/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.cache.redis.delay.message;

import com.kuma.boot.cache.redis.delay.MessageConversionException;

import java.util.Map;
import java.util.UUID;

public class DefaultRedissonMessageConverter
implements MessageConverter {
    @Override
    public QueueMessage<?> toMessage(Object payload, Map<String, Object> headers) {
        headers.put("message_id", UUID.randomUUID().toString());
        return QueueMessageBuilder.withPayload(payload).headers(headers).build();
    }

    @Override
    public Object fromMessage(RedissonMessage redissonMessage) throws MessageConversionException {
        return redissonMessage.getPayload();
    }
}

