package com.kuma.boot.eventbus.disruptor.tmp5.event;

import org.springframework.context.ApplicationEvent;

public class ThreadPoolExecutorErrorEvent extends ApplicationEvent {
   private Runnable r;
   private Throwable t;

   public ThreadPoolExecutorErrorEvent(Object source, Runnable r, Throwable t) {
      super(source);
      this.r = r;
      this.t = t;
   }

   public Runnable getR() {
      return this.r;
   }

   public Throwable getT() {
      return this.t;
   }
}
