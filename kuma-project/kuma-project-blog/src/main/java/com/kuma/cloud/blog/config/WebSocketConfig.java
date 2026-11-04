package com.kuma.cloud.blog.config;

import com.kuma.boot.websocket.stomp.autoconfigure.WebSocketStompEndpointCustomizer;
import com.kuma.boot.websocket.stomp.autoconfigure.MessageWebSocketAutoConfiguration;
import com.kuma.cloud.blog.security.BlogJwtAuthenticationConverter;
import com.kuma.cloud.blog.websocket.ChatAccessService;
import com.kuma.cloud.blog.websocket.ChatChannelInterceptor;
import com.kuma.cloud.blog.websocket.ChatHandshakeInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

/** Chat authentication is business-specific; the starter owns STOMP transport and broker setup. */
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration(MessageWebSocketAutoConfiguration.class)
public class WebSocketConfig {
    @Bean
    ChatHandshakeInterceptor chatHandshakeInterceptor(JwtDecoder decoder,
            BlogJwtAuthenticationConverter converter, ChatAccessService access) {
        return new ChatHandshakeInterceptor(decoder, converter, access);
    }

    @Bean
    ChatChannelInterceptor chatChannelInterceptor(JwtDecoder decoder,
            BlogJwtAuthenticationConverter converter, ChatAccessService access) {
        return new ChatChannelInterceptor(decoder, converter, access);
    }

    @Bean
    WebSocketMessageBrokerConfigurer chatChannelSecurity(ChatChannelInterceptor interceptor) {
        return new WebSocketMessageBrokerConfigurer() {
            @Override
            public void configureClientInboundChannel(ChannelRegistration registration) {
                registration.interceptors(interceptor);
            }

            @Override
            public void configureClientOutboundChannel(ChannelRegistration registration) {
                registration.interceptors(new ChannelInterceptor() {
                    @Override
                    public Message<?> preSend(Message<?> message, MessageChannel channel) {
                        return interceptor.filterOutbound(message);
                    }
                });
            }
        };
    }

    @Bean
    WebSocketStompEndpointCustomizer chatWebSocketEndpointCustomizer(ChatHandshakeInterceptor interceptor) {
        return endpoint -> endpoint.addInterceptors(interceptor);
    }
}
