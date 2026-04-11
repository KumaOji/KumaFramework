package com.kuma.boot.websocket.spring.common.handler;

import com.kuma.boot.websocket.spring.common.WebSocketMessageSender;
import com.kuma.boot.websocket.spring.common.message.JsonWebSocketMessage;
import com.kuma.boot.websocket.spring.common.message.PingJsonWebSocketMessage;
import com.kuma.boot.websocket.spring.common.message.PongJsonWebSocketMessage;
import com.kuma.boot.websocket.spring.common.message.WebSocketMessageTypeEnum;
import org.springframework.web.socket.WebSocketSession;

public class PingJsonMessageHandler implements JsonMessageHandler<PingJsonWebSocketMessage> {
   public PingJsonMessageHandler() {
   }

   public void handle(WebSocketSession session, PingJsonWebSocketMessage message) {
      JsonWebSocketMessage pongJsonWebSocketMessage = new PongJsonWebSocketMessage();
      WebSocketMessageSender.send(session, pongJsonWebSocketMessage);
   }

   public String type() {
      return WebSocketMessageTypeEnum.PING.getValue();
   }

   public Class<PingJsonWebSocketMessage> getMessageClass() {
      return PingJsonWebSocketMessage.class;
   }
}
