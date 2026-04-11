package com.kuma.boot.eventbus.disruptor.tmp3.event;

import java.util.concurrent.ThreadFactory;

public class DisruptorEventDaemonThreadFactory implements ThreadFactory {
   public DisruptorEventDaemonThreadFactory() {
   }

   public Thread newThread(Runnable r) {
      Thread t = new Thread(r);
      t.setName("kmc-disruptor-threadpool");
      t.setDaemon(true);
      return t;
   }
}
