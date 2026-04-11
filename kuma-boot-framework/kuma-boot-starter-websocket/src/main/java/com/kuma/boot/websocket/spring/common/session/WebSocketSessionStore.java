package com.kuma.boot.websocket.spring.common.session;

import java.util.Collection;
import org.springframework.web.socket.WebSocketSession;

public interface WebSocketSessionStore {
   void addSession(WebSocketSession session);

   void removeSession(WebSocketSession session);

   Collection<WebSocketSession> getSessions();

   Collection<WebSocketSession> getSessions(Object sessionKey);

   Collection<Object> getSessionKeys();
}
