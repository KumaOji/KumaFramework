package com.kuma.boot.websocket.original;

public class SendMsg extends SendMsgAll {
   private String userId;

   public SendMsg() {
   }

   public String getUserId() {
      return this.userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }
}
