package com.kuma.boot.websocket.original;

import jakarta.websocket.Session;
import java.util.concurrent.atomic.AtomicInteger;

public class WebSocketBean {
   private Session session;
   private AtomicInteger erroerLinkCount = new AtomicInteger(0);

   public WebSocketBean() {
   }

   public int getErroerLinkCount() {
      return this.erroerLinkCount.getAndIncrement();
   }

   public void cleanErrorNum() {
      this.erroerLinkCount = new AtomicInteger(0);
   }

   public Session getSession() {
      return this.session;
   }

   public void setSession(Session session) {
      this.session = session;
   }
}
