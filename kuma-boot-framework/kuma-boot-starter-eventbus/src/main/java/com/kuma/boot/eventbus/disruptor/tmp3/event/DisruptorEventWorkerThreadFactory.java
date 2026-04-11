package com.kuma.boot.eventbus.disruptor.tmp3.event;

import java.util.concurrent.ThreadFactory;

public class DisruptorEventWorkerThreadFactory implements ThreadFactory {
   private int counter = 0;
   private String prefix;

   public DisruptorEventWorkerThreadFactory(String prefix) {
      this.prefix = prefix;
   }

   public Thread newThread(Runnable r) {
      String var10003 = this.prefix;
      return new Thread(r, var10003 + "-" + this.counter++);
   }
}
