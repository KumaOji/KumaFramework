package com.kuma.boot.websocket.spring.common.session;

import com.kuma.boot.websocket.spring.common.handler.ConcurrentWebSocketSessionOptions;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

public class MapSessionWebSocketHandlerDecorator extends WebSocketHandlerDecorator {
   private final WebSocketSessionStore webSocketSessionStore;
   private final ConcurrentWebSocketSessionOptions concurrentWebSocketSessionOptions;

   public MapSessionWebSocketHandlerDecorator(WebSocketHandler delegate, WebSocketSessionStore webSocketSessionStore, ConcurrentWebSocketSessionOptions concurrentWebSocketSessionOptions) {
      super(delegate);
      this.webSocketSessionStore = webSocketSessionStore;
      this.concurrentWebSocketSessionOptions = concurrentWebSocketSessionOptions;
   }

   public void afterConnectionEstablished(WebSocketSession wsSession) throws Exception {
      if (Boolean.TRUE.equals(this.concurrentWebSocketSessionOptions.isEnable())) {
         wsSession = new ConcurrentWebSocketSessionDecorator(wsSession, this.concurrentWebSocketSessionOptions.getSendTimeLimit(), this.concurrentWebSocketSessionOptions.getBufferSizeLimit(), this.concurrentWebSocketSessionOptions.getOverflowStrategy());
      }

      this.webSocketSessionStore.addSession(wsSession);
   }

   public void afterConnectionClosed(WebSocketSession wsSession, CloseStatus closeStatus) throws Exception {
      this.webSocketSessionStore.removeSession(wsSession);
   }
}
