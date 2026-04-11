package com.kuma.boot.websocket.spring.websocketsimple;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

public class WebSocketSessionHandlerDecorator extends WebSocketHandlerDecorator {
   private static final Integer SEND_TIME_LIMIT = 5000;
   private static final Integer BUFFER_SIZE_LIMIT = 102400;
   private final WebSocketEmitterService webSocketEmitterService;

   public WebSocketSessionHandlerDecorator(WebSocketHandler delegate, WebSocketEmitterService webSocketEmitterService) {
      super(delegate);
      this.webSocketEmitterService = webSocketEmitterService;
   }

   public void afterConnectionEstablished(WebSocketSession session) {
      WebSocketSession var2 = new ConcurrentWebSocketSessionDecorator(session, SEND_TIME_LIMIT, BUFFER_SIZE_LIMIT);
      this.webSocketEmitterService.addSession(var2);
   }

   public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
      this.webSocketEmitterService.removeSession(session);
   }
}
