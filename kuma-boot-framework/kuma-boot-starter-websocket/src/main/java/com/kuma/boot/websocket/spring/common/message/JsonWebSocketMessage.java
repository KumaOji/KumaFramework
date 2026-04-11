package com.kuma.boot.websocket.spring.common.message;

public abstract class JsonWebSocketMessage {
   public static final String TYPE_FIELD = "type";
   private final String type;

   protected JsonWebSocketMessage(String type) {
      this.type = type;
   }

   public String getType() {
      return this.type;
   }
}
