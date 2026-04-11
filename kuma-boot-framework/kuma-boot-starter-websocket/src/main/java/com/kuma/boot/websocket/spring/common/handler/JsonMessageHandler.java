package com.kuma.boot.websocket.spring.common.handler;

import com.kuma.boot.websocket.spring.common.message.JsonWebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public interface JsonMessageHandler<T extends JsonWebSocketMessage> {
   void handle(WebSocketSession session, T message);

   String type();

   Class<T> getMessageClass();
}
