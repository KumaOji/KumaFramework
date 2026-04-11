package com.kuma.boot.websocket.spring.common.message;

public class PingJsonWebSocketMessage extends JsonWebSocketMessage {
   public PingJsonWebSocketMessage() {
      super(WebSocketMessageTypeEnum.PING.getValue());
   }
}
