package com.kuma.boot.eventbus.disruptor.tmp1.consumer;

public abstract class QueueConsumerExecutor<T> implements Runnable {
   private T data;

   public QueueConsumerExecutor() {
   }

   public T getData() {
      return this.data;
   }

   public void setData(final T data) {
      this.data = data;
   }
}
