package com.kuma.boot.eventbus.disruptor.tmp3.event;

import java.util.concurrent.ThreadFactory;

public class DisruptorEventMaxPriorityThreadFactory implements ThreadFactory {
   public DisruptorEventMaxPriorityThreadFactory() {
   }

   public Thread newThread(Runnable r) {
      Thread t = new Thread(r);
      t.setName("kmc-disruptor-event-priority-threadpool");
      t.setPriority(10);
      return t;
   }
}
