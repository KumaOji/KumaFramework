package com.kuma.boot.websocket.stomp.autoconfigure;

import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

/** Business handshake interceptors/handlers applied before SockJS is enabled. Supports Ordered/@Order. */
@FunctionalInterface
public interface WebSocketStompEndpointCustomizer {
    void customize(StompWebSocketEndpointRegistration endpoint);
}
