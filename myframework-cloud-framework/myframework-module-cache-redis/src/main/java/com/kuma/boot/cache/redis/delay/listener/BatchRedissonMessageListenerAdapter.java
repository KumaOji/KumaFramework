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
import com.kuma.boot.cache.redis.delay.handler.RedissonListenerErrorHandler;
import com.kuma.boot.cache.redis.delay.message.MessageConverter;
import com.kuma.boot.cache.redis.delay.message.QueueMessage;
import com.kuma.boot.cache.redis.delay.message.RedissonHeaders;
import com.kuma.boot.cache.redis.delay.message.RedissonMessage;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BatchRedissonMessageListenerAdapter
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-02-18 10:36:41
 */
public class BatchRedissonMessageListenerAdapter extends AbstractRedissonMessageListenerAdapter<List<RedissonMessage>> {

    private final InvocableHandlerMethod invocableHandlerMethod;

    private final RedissonListenerErrorHandler errorHandler;

    private BatchMessagingMessageConverter batchMessagingMessageConverter;

    public BatchRedissonMessageListenerAdapter(
            InvocableHandlerMethod invocableHandlerMethod, MessageConverter messageConverter ) {
        this(invocableHandlerMethod, messageConverter, null);
    }

    public BatchRedissonMessageListenerAdapter(
            InvocableHandlerMethod invocableHandlerMethod,
            MessageConverter messageConverter,
            RedissonListenerErrorHandler errorHandler ) {
        this.invocableHandlerMethod = invocableHandlerMethod;
        this.errorHandler = errorHandler;
        MessageConverter payloadConverter = messageConverter;
        if (payloadConverter == null) {
            payloadConverter = new SimpleMessageConverter();
        }
        this.batchMessagingMessageConverter = new BatchMessagingMessageConverter(payloadConverter);
    }

    @Override
    public void onMessage( List<RedissonMessage> redissonMessage ) throws Exception {
        Message<?> message = this.batchMessagingMessageConverter.fromMessage(redissonMessage);
        try {
            this.invocableHandlerMethod.invoke(message);
        } catch (Exception e) {
            if (this.errorHandler != null) {
                this.errorHandler.handleError(null, message, e);
            } else {
                throw e;
            }
        }
    }

    /**
     * BatchMessagingMessageConverter
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    private static class BatchMessagingMessageConverter implements BatchMessageConverter {

        private final MessageConverter payloadConverter;

        private BatchMessagingMessageConverter( MessageConverter payloadConverter ) {
            Assert.notNull(payloadConverter, "payloadConverter must not be null");
            this.payloadConverter = payloadConverter;
        }

        @Override
        public QueueMessage<?> toMessage( Object payload, Map<String, Object> headers )
                throws MessageConversionException {
            return null;
        }

        @Override
        public List<QueueMessage<?>> toListMessage( Object payload, Map<String, Object> headers ) {
            return null;
        }

        @Override
        public Message<?> fromMessage( List<RedissonMessage> redissonMessages ) throws MessageConversionException {
            List<Object> payloads = new ArrayList<>();
            Map<String, Object> headers = new HashMap<>(4);
            List<Map<String, Object>> batchConvertedHeaders = new ArrayList<>();
            headers.put(RedissonHeaders.RECEIVED_TIMESTAMP, System.currentTimeMillis());
            headers.put(RedissonHeaders.BATCH_CONVERTED_HEADERS, batchConvertedHeaders);
            redissonMessages.forEach(redissonMessage -> {
                Map<String, Object> rawHeaders = redissonMessage.getHeaders();
                Object convertedPayload = this.payloadConverter.fromMessage(redissonMessage);
                Object payload = convertedPayload;
                Map<String, Object> convertedHeaders = rawHeaders;
                if (convertedPayload instanceof Message) {
                    @SuppressWarnings("unchecked")
                    Message<Object> convertedMessage = (Message<Object>) convertedPayload;
                    payload = convertedMessage.getPayload();
                    convertedHeaders = convertedMessage.getHeaders();
                }
                payloads.add(payload);
                batchConvertedHeaders.add(convertedHeaders);
                headers.putIfAbsent(
                        RedissonHeaders.DELIVERY_QUEUE_NAME, rawHeaders.get(RedissonHeaders.DELIVERY_QUEUE_NAME));
            });
            return new GenericMessage<>(payloads, headers);
        }
    }
}
