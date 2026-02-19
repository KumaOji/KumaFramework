/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.cache.redis.delay.listener;

import com.kuma.boot.cache.redis.delay.MessageConversionException;
import com.kuma.boot.cache.redis.delay.message.MessageConverter;
import com.kuma.boot.cache.redis.delay.message.QueueMessage;
import com.kuma.boot.cache.redis.delay.message.RedissonMessage;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class AbstractRedissonMessageListenerAdapter<T>
implements RedissonMessageListener<T> {

    protected static class SimpleMessageConverter
    implements MessageConverter {
        protected SimpleMessageConverter() {
        }

        @Override
        public QueueMessage<?> toMessage(Object payload, Map<String, Object> headers) throws MessageConversionException {
            return null;
        }

        @Override
        public String fromMessage(RedissonMessage redissonMessage) throws MessageConversionException {
            String charset = (String)redissonMessage.getHeaders().getOrDefault("charset_name", StandardCharsets.UTF_8.name());
            return new String(redissonMessage.getPayload());
        }
    }
}

