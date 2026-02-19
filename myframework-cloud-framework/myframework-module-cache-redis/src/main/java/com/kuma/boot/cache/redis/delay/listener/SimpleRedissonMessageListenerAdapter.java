/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.messaging.Message
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
import java.util.Map;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.util.Assert;

public class SimpleRedissonMessageListenerAdapter
extends AbstractRedissonMessageListenerAdapter<RedissonMessage> {
    private final InvocableHandlerMethod invocableHandlerMethod;
    private final RedissonListenerErrorHandler errorHandler;
    private MessagingMessageConverter messagingMessageConverter;

    public SimpleRedissonMessageListenerAdapter(InvocableHandlerMethod invocableHandlerMethod, MessageConverter messageConverter) {
        this(invocableHandlerMethod, messageConverter, null);
    }

    public SimpleRedissonMessageListenerAdapter(InvocableHandlerMethod invocableHandlerMethod, MessageConverter messageConverter, RedissonListenerErrorHandler errorHandler) {
        this.invocableHandlerMethod = invocableHandlerMethod;
        this.errorHandler = errorHandler;
        MessageConverter payloadConverter = messageConverter;
        if (payloadConverter == null) {
            payloadConverter = new AbstractRedissonMessageListenerAdapter.SimpleMessageConverter();
        }
        this.messagingMessageConverter = new MessagingMessageConverter(payloadConverter);
    }

    @Override
    public void onMessage(RedissonMessage redissonMessage) throws Exception {
        Message message = this.messagingMessageConverter.fromMessage(redissonMessage);
        try {
            this.invocableHandlerMethod.invoke(message, new Object[]{redissonMessage});
        }
        catch (Exception e) {
            if (this.errorHandler != null) {
                this.errorHandler.handleError(redissonMessage, message, e);
            }
            throw e;
        }
    }

    private static class MessagingMessageConverter
    implements MessageConverter {
        private final MessageConverter payloadConverter;

        private MessagingMessageConverter(MessageConverter payloadConverter) {
            Assert.notNull((Object)payloadConverter, (String)"payloadConverter must not be null");
            this.payloadConverter = payloadConverter;
        }

        @Override
        public QueueMessage<?> toMessage(Object payload, Map<String, Object> headers) throws MessageConversionException {
            return null;
        }

        public Message fromMessage(RedissonMessage redissonMessage) throws MessageConversionException {
            Object convertedPayload = this.payloadConverter.fromMessage(redissonMessage);
            if (convertedPayload instanceof Message) {
                return (Message)convertedPayload;
            }
            return new GenericMessage(convertedPayload, redissonMessage.getHeaders());
        }
    }
}

