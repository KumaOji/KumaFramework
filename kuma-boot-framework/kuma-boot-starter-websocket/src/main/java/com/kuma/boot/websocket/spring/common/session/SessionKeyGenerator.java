package com.kuma.boot.websocket.spring.common.session;

import org.springframework.web.socket.WebSocketSession;

public interface SessionKeyGenerator {
   Object sessionKey(WebSocketSession webSocketSession);
}
