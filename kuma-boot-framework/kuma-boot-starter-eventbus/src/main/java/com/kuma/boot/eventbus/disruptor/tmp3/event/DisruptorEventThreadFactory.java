package com.kuma.boot.eventbus.disruptor.tmp3.event;

import java.util.concurrent.ThreadFactory;

public class DisruptorEventThreadFactory implements ThreadFactory {
   public DisruptorEventThreadFactory() {
   }

   public Thread newThread(Runnable r) {
      Thread t = new Thread(r);
      t.setName("kmc-disruptor-event-threadpool");
      return t;
   }
}
