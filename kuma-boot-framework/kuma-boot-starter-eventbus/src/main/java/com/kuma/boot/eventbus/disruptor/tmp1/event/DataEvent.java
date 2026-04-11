package com.kuma.boot.eventbus.disruptor.tmp1.event;

public class DataEvent<T> {
   private T data;

   public DataEvent() {
   }

   public T getData() {
      return this.data;
   }

   public void setData(final T data) {
      this.data = data;
   }
}
