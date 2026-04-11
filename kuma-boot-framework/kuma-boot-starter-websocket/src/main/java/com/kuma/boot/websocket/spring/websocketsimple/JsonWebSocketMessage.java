package com.kuma.boot.websocket.spring.websocketsimple;

import java.io.Serializable;

public class JsonWebSocketMessage implements Serializable {
   private String type;
   private String content;

   public JsonWebSocketMessage() {
   }

   public String getType() {
      return this.type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getContent() {
      return this.content;
   }

   public void setContent(String content) {
      this.content = content;
   }
}
