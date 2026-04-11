package com.kuma.boot.websocket.stomp.domain;

import java.io.Serializable;

public class WebSocketMessage<T> implements Serializable {
   private String to;
   private String channel;
   private T payload;

   public WebSocketMessage() {
   }

   public String getTo() {
      return this.to;
   }

   public void setTo(String to) {
      this.to = to;
   }

   public String getChannel() {
      return this.channel;
   }

   public void setChannel(String channel) {
      this.channel = channel;
   }

   public T getPayload() {
      return this.payload;
   }

   public void setPayload(T payload) {
      this.payload = payload;
   }
}
