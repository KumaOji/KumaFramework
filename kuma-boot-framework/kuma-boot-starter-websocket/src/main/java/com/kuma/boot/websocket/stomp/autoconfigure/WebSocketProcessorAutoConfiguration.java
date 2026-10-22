package com.kuma.boot.websocket.stomp.autoconfigure;

import com.kuma.boot.websocket.stomp.autoconfigure.properties.WebSocketStompProperties;
import com.kuma.boot.websocket.stomp.core.BearerTokenResolver;
import com.kuma.boot.websocket.stomp.interceptor.WebSocketChannelInterceptor;
import com.kuma.boot.websocket.stomp.interceptor.WebSocketSessionHandshakeInterceptor;
import com.kuma.boot.websocket.stomp.processor.WebSocketClusterProcessor;
import com.kuma.boot.websocket.stomp.processor.WebSocketMessageSender;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;

@Configuration(proxyBeanMethods = false)
@Import(WebSocketProcessorAutoConfiguration.RedisClusterConfiguration.class)
public class WebSocketProcessorAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = WebSocketStompProperties.PREFIX, name = "principal-authentication-enabled", havingValue = "true")
    public WebSocketChannelInterceptor webSocketChannelInterceptor() {
        return new WebSocketChannelInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = WebSocketStompProperties.PREFIX, name = "principal-authentication-enabled", havingValue = "true")
    public WebSocketSessionHandshakeInterceptor webSocketSessionHandshakeInterceptor(BearerTokenResolver resolver) {
        // A real application resolver is required; never treat an unverified token as an identity.
        return new WebSocketSessionHandshakeInterceptor(resolver);
    }

    @Bean
    @ConditionalOnMissingBean
    public WebSocketMessageSender webSocketMessageSender(SimpMessagingTemplate template, SimpUserRegistry registry) {
        var sender = new WebSocketMessageSender();
        sender.setSimpMessagingTemplate(template);
        sender.setSimpUserRegistry(registry);
        return sender;
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "org.redisson.api.RedissonClient")
    @ConditionalOnBean(type = "org.redisson.api.RedissonClient")
    @ConditionalOnProperty(prefix = WebSocketStompProperties.PREFIX, name = "cluster-enabled", havingValue = "true")
    static class RedisClusterConfiguration {
        @Bean
        @ConditionalOnMissingBean
        WebSocketClusterProcessor webSocketClusterProcessor(WebSocketStompProperties properties,
                WebSocketMessageSender sender, RedissonClient redisson) {
            var processor = new WebSocketClusterProcessor();
            processor.setWebSocketProperties(properties);
            processor.setWebSocketMessageSender(sender);
            processor.setRedissonClient(redisson);
            return processor;
        }
    }
}
