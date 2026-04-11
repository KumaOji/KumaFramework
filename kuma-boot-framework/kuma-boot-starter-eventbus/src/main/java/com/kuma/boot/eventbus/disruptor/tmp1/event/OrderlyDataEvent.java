package com.kuma.boot.eventbus.disruptor.tmp1.event;

public class OrderlyDataEvent<T> extends DataEvent<T> {
   private String hash;

   public OrderlyDataEvent() {
   }

   public String getHash() {
      return this.hash;
   }

   public void setHash(final String hash) {
      this.hash = hash;
   }
}
