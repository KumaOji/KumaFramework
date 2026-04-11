package com.kuma.boot.websocket.spring.websocketsimple;

import org.springframework.web.socket.WebSocketSession;

public interface WebSocketMessageListener<T> {
   void onMessage(WebSocketSession session, T message);

   String getType();
}
