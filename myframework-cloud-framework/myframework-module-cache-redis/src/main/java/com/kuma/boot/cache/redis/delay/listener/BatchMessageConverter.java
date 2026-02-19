/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.messaging.Message
 */
package com.kuma.boot.cache.redis.delay.listener;

import com.kuma.boot.cache.redis.delay.MessageConversionException;
import com.kuma.boot.cache.redis.delay.message.MessageConverter;
import com.kuma.boot.cache.redis.delay.message.QueueMessage;
import com.kuma.boot.cache.redis.delay.message.RedissonMessage;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.messaging.Message;

public interface BatchMessageConverter
extends MessageConverter {
    @Override
    default public QueueMessage<?> toMessage(Object payload, Map<String, Object> headers) throws MessageConversionException {
        throw new UnsupportedOperationException("please see [toListMessage] method");
    }

    @Override
    default public Object fromMessage(RedissonMessage redissonMessage) throws MessageConversionException {
        return this.fromMessage(Collections.singletonList(redissonMessage));
    }

    public List<QueueMessage<?>> toListMessage(Object var1, Map<String, Object> var2);

    public Message<?> fromMessage(List<RedissonMessage> var1) throws MessageConversionException;
}

