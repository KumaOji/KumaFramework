package com.kuma.cloud.blog.config;

import com.kuma.boot.websocket.stomp.autoconfigure.WebSocketStompEndpointCustomizer;
import com.kuma.boot.websocket.stomp.autoconfigure.MessageWebSocketAutoConfiguration;
import com.kuma.cloud.blog.security.BlogJwtAuthenticationConverter;
import com.kuma.cloud.blog.service.ChatBlacklistService;
import com.kuma.cloud.blog.websocket.ChatHandshakeInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.security.oauth2.jwt.JwtDecoder;

/** Chat authentication is business-specific; the starter owns STOMP transport and broker setup. */
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration(MessageWebSocketAutoConfiguration.class)
public class WebSocketConfig {
    @Bean
    ChatHandshakeInterceptor chatHandshakeInterceptor(JwtDecoder decoder,
            BlogJwtAuthenticationConverter converter, ChatBlacklistService blacklist) {
        return new ChatHandshakeInterceptor(decoder, converter, blacklist);
    }

    @Bean
    WebSocketStompEndpointCustomizer chatWebSocketEndpointCustomizer(ChatHandshakeInterceptor interceptor) {
        return endpoint -> endpoint.addInterceptors(interceptor);
    }
}
