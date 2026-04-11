package com.kuma.boot.eventbus.disruptor.tmp.support;

public class DisruptorEventWrapper<V> implements GenericWrapper<V> {
   private V real;

   public DisruptorEventWrapper() {
   }

   public void wrap(V unwrap) {
      if (this.real == null) {
         this.real = unwrap;
      }

   }

   public V unwrap() {
      return this.real;
   }
}
