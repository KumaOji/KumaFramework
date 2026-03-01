/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.integration.config.GlobalChannelInterceptor
 *  org.springframework.messaging.Message
 *  org.springframework.messaging.MessageChannel
 *  org.springframework.messaging.support.ChannelInterceptor
 *  org.springframework.stereotype.Component
 */
package com.kuma.cloud.stream.interceptor;

import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@GlobalChannelInterceptor(patterns={"*"})
public class MyInterceptor
implements ChannelInterceptor {
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        System.out.println(message.getHeaders());
        Object payload = message.getPayload();
        if (payload instanceof byte[]) {
            byte[] data = (byte[])payload;
            System.out.println(new String(data));
        } else {
            System.out.println(message.getPayload());
        }
        return message;
    }
}

