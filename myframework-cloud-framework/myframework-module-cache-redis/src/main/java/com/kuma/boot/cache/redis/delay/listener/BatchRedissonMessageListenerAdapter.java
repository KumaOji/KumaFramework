/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.messaging.Message
 *  org.springframework.messaging.MessageHeaders
 *  org.springframework.messaging.handler.invocation.InvocableHandlerMethod
 *  org.springframework.messaging.support.GenericMessage
 *  org.springframework.util.Assert
 */
package com.kuma.boot.cache.redis.delay.listener;

import com.kuma.boot.cache.redis.delay.MessageConversionException;
import com.kuma.boot.cache.redis.delay.handler.RedissonListenerErrorHandler;
import com.kuma.boot.cache.redis.delay.message.MessageConverter;
import com.kuma.boot.cache.redis.delay.message.QueueMessage;
import com.kuma.boot.cache.redis.delay.message.RedissonMessage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.util.Assert;

public class BatchRedissonMessageListenerAdapter
extends AbstractRedissonMessageListenerAdapter<List<RedissonMessage>> {
    private final InvocableHandlerMethod invocableHandlerMethod;
    private final RedissonListenerErrorHandler errorHandler;
    private BatchMessagingMessageConverter batchMessagingMessageConverter;

    public BatchRedissonMessageListenerAdapter(InvocableHandlerMethod invocableHandlerMethod, MessageConverter messageConverter) {
        this(invocableHandlerMethod, messageConverter, null);
    }

    public BatchRedissonMessageListenerAdapter(InvocableHandlerMethod invocableHandlerMethod, MessageConverter messageConverter, RedissonListenerErrorHandler errorHandler) {
        this.invocableHandlerMethod = invocableHandlerMethod;
        this.errorHandler = errorHandler;
        MessageConverter payloadConverter = messageConverter;
        if (payloadConverter == null) {
            payloadConverter = new AbstractRedissonMessageListenerAdapter.SimpleMessageConverter();
        }
        this.batchMessagingMessageConverter = new BatchMessagingMessageConverter(payloadConverter);
    }

    @Override
    public void onMessage(List<RedissonMessage> redissonMessage) throws Exception {
        Message<?> message = this.batchMessagingMessageConverter.fromMessage(redissonMessage);
        try {
            this.invocableHandlerMethod.invoke(message, new Object[0]);
        }
        catch (Exception e) {
            if (this.errorHandler != null) {
                this.errorHandler.handleError(null, message, e);
            }
            throw e;
        }
    }

    private static class BatchMessagingMessageConverter
    implements BatchMessageConverter {
        private final MessageConverter payloadConverter;

        private BatchMessagingMessageConverter(MessageConverter payloadConverter) {
            Assert.notNull((Object)payloadConverter, (String)"payloadConverter must not be null");
            this.payloadConverter = payloadConverter;
        }

        @Override
        public QueueMessage<?> toMessage(Object payload, Map<String, Object> headers) throws MessageConversionException {
            return null;
        }

        @Override
        public List<QueueMessage<?>> toListMessage(Object payload, Map<String, Object> headers) {
            return null;
        }

        @Override
        public Message<?> fromMessage(List<RedissonMessage> redissonMessages) throws MessageConversionException {
            ArrayList payloads = new ArrayList();
            HashMap<String, Serializable> headers = new HashMap<String, Serializable>(4);
            ArrayList batchConvertedHeaders = new ArrayList();
            headers.put("received_timestamp", Long.valueOf(System.currentTimeMillis()));
            headers.put("batch_converted_headers", batchConvertedHeaders);
            redissonMessages.forEach(redissonMessage -> {
                Object convertedPayload;
                MessageHeaders rawHeaders = redissonMessage.getHeaders();
                Object payload = convertedPayload = this.payloadConverter.fromMessage((RedissonMessage)redissonMessage);
                MessageHeaders convertedHeaders = rawHeaders;
                if (convertedPayload instanceof Message) {
                    Message convertedMessage = (Message)convertedPayload;
                    payload = convertedMessage.getPayload();
                    convertedHeaders = convertedMessage.getHeaders();
                }
                payloads.add(payload);
                batchConvertedHeaders.add(convertedHeaders);
                headers.putIfAbsent("delivery_queue_name", (Serializable)rawHeaders.get("delivery_queue_name"));
            });
            return new GenericMessage(payloads, headers);
        }
    }
}

