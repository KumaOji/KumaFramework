/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.cache.redis.delay.listener;

import com.kuma.boot.cache.redis.delay.MessageConversionException;
import com.kuma.boot.cache.redis.delay.message.MessageConverter;
import com.kuma.boot.cache.redis.delay.message.QueueMessage;
import com.kuma.boot.cache.redis.delay.message.RedissonHeaders;
import com.kuma.boot.cache.redis.delay.message.RedissonMessage;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * AbstractRedissonMessageListenerAdapter
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-02-18 10:36:41
 */
public abstract class AbstractRedissonMessageListenerAdapter<T> implements RedissonMessageListener<T> {

    /**
     * SimpleMessageConverter
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    protected static class SimpleMessageConverter implements MessageConverter {

        @Override
        public QueueMessage<?> toMessage( Object payload, Map<String, Object> headers )
                throws MessageConversionException {
            return null;
        }

        @Override
        public String fromMessage( RedissonMessage redissonMessage ) throws MessageConversionException {
            String charset = (String) redissonMessage
                    .getHeaders()
                    .getOrDefault(RedissonHeaders.CHARSET_NAME, StandardCharsets.UTF_8.name());
            return new String(redissonMessage.getPayload());
        }
    }
}
