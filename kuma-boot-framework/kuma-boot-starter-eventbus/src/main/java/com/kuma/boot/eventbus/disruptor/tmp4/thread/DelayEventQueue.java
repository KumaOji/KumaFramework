package com.kuma.boot.eventbus.disruptor.tmp4.thread;

import java.util.concurrent.LinkedBlockingQueue;

public class DelayEventQueue<T> {
   private final LinkedBlockingQueue<T> queue = new LinkedBlockingQueue();

   public DelayEventQueue() {
   }

   public T take() throws InterruptedException {
      return (T)this.queue.take();
   }

   public void put(T t) throws InterruptedException {
      this.queue.put(t);
   }

   public boolean isEmpty() {
      return this.queue.isEmpty();
   }
}
