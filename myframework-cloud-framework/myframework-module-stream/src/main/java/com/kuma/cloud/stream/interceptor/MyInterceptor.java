package com.kuma.cloud.stream.interceptor;

import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

//使用Interceptor
// 因为StreamBridge使用MessageChannel来建立发布的binding，所以可以利用ChannelInterceptor来拦截发布过程。比如：

//参数patterns的值可以控制该拦截器的硬性范围
//
// *表示拦截所有binding
// foo-*表示只拦截那些以foo-开头的binding
@Component
@GlobalChannelInterceptor(patterns = "*")
public class MyInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        System.out.println(message.getHeaders());
        Object payload = message.getPayload();
        if (payload instanceof byte[] data) {
            System.out.println(new String(data));
        } else {
            System.out.println(message.getPayload());
        }
        return message;
    }
}
