package com.kuma.boot.websocket.stomp.autoconfigure;

import com.kuma.boot.websocket.stomp.autoconfigure.properties.WebSocketStompProperties;
import com.kuma.boot.websocket.stomp.interceptor.WebSocketChannelInterceptor;
import com.kuma.boot.websocket.stomp.interceptor.WebSocketPrincipalHandshakeHandler;
import com.kuma.boot.websocket.stomp.interceptor.WebSocketSessionHandshakeInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.*;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerAutoConfiguration implements WebSocketMessageBrokerConfigurer {
    private final WebSocketStompProperties properties;
    private final ObjectProvider<WebSocketChannelInterceptor> channelInterceptor;
    private final ObjectProvider<WebSocketSessionHandshakeInterceptor> handshakeInterceptor;
    private final List<WebSocketStompEndpointCustomizer> customizers;
    private final ThreadPoolTaskScheduler heartbeatScheduler;

    public WebSocketMessageBrokerAutoConfiguration(WebSocketStompProperties properties,
            ObjectProvider<WebSocketChannelInterceptor> channelInterceptor,
            ObjectProvider<WebSocketSessionHandshakeInterceptor> handshakeInterceptor,
            ObjectProvider<WebSocketStompEndpointCustomizer> customizers,
            @org.springframework.beans.factory.annotation.Qualifier("kumaWebSocketHeartbeatScheduler")
            ThreadPoolTaskScheduler heartbeatScheduler) {
        this.properties = properties;
        this.channelInterceptor = channelInterceptor;
        this.handshakeInterceptor = handshakeInterceptor;
        this.customizers = customizers.orderedStream().toList();
        this.heartbeatScheduler = heartbeatScheduler;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        if (!properties.isSockjsEnabled() && !properties.isNativeEndpointEnabled()) {
            throw new IllegalArgumentException("Enable at least one STOMP endpoint transport");
        }
        if (properties.isSockjsEnabled()) {
            configureEndpoint(registry.addEndpoint(properties.getEndpoint())).withSockJS();
        }
        if (properties.isNativeEndpointEnabled()) {
            configureEndpoint(registry.addEndpoint(properties.getEndpoint()));
        }
    }

    private StompWebSocketEndpointRegistration configureEndpoint(StompWebSocketEndpointRegistration endpoint) {
        endpoint.setAllowedOriginPatterns(properties.getAllowedOriginPatterns().toArray(String[]::new));
        if (properties.isPrincipalAuthenticationEnabled()) {
            endpoint.addInterceptors(handshakeInterceptor.getObject());
            endpoint.setHandshakeHandler(new WebSocketPrincipalHandshakeHandler());
        }
        customizers.forEach(customizer -> customizer.customize(endpoint));
        return endpoint;
    }

    // Spring manages scheduler shutdown with the broker lifecycle.
    @Bean
    public static ThreadPoolTaskScheduler kumaWebSocketHeartbeatScheduler() {
        var scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("kuma-websocket-heartbeat-");
        scheduler.setRemoveOnCancelPolicy(true);
        return scheduler;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        var broker = registry.enableSimpleBroker(properties.getBrokerPrefixes().toArray(String[]::new));
        long heartbeat = properties.getHeartbeatInterval();
        if (heartbeat < 0) throw new IllegalArgumentException("STOMP heartbeat interval must be non-negative");
        broker.setHeartbeatValue(new long[]{heartbeat, heartbeat});
        if (heartbeat > 0) broker.setTaskScheduler(heartbeatScheduler);
        registry.setApplicationDestinationPrefixes(properties.getApplicationPrefixes());
        if (properties.getUserDestinationPrefix() != null && !properties.getUserDestinationPrefix().isBlank()) {
            registry.setUserDestinationPrefix(properties.getUserDestinationPrefix());
        }
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(properties.getMessageSizeLimit())
                .setSendBufferSizeLimit(properties.getSendBufferSizeLimit())
                .setSendTimeLimit(properties.getSendTimeLimit());
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        if (properties.isPrincipalAuthenticationEnabled()) registration.interceptors(channelInterceptor.getObject());
    }
}
