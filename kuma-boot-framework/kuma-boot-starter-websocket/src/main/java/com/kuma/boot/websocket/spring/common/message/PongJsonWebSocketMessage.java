package com.kuma.boot.websocket.spring.common.message;

public class PongJsonWebSocketMessage extends JsonWebSocketMessage {
   public PongJsonWebSocketMessage() {
      super(WebSocketMessageTypeEnum.PONG.getValue());
   }
}
