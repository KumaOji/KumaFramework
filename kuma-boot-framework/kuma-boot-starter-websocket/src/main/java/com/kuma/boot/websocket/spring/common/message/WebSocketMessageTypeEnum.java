package com.kuma.boot.websocket.spring.common.message;

public enum WebSocketMessageTypeEnum {
   PING("ping"),
   PONG("pong");

   private final String value;

   private WebSocketMessageTypeEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }

   // $FF: synthetic method
   private static WebSocketMessageTypeEnum[] $values() {
      return new WebSocketMessageTypeEnum[]{PING, PONG};
   }
}
