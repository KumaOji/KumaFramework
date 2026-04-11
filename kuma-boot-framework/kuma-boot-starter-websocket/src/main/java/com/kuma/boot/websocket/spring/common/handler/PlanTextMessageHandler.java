package com.kuma.boot.websocket.spring.common.handler;

import org.springframework.web.socket.WebSocketSession;

public interface PlanTextMessageHandler {
   void handle(WebSocketSession session, String message);
}
