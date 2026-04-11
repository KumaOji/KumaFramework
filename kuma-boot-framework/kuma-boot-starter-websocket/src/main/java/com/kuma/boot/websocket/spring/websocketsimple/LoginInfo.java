package com.kuma.boot.websocket.spring.websocketsimple;

public class LoginInfo {
   private String id;
   private String type;

   public LoginInfo() {
   }

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getType() {
      return this.type;
   }

   public void setType(String type) {
      this.type = type;
   }
}
