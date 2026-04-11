package com.kuma.boot.eventbus.disruptor.tmp3.event;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import org.slf4j.LoggerFactory;

public class DisruptorEventLoggerThreadFactory implements ThreadFactory {
   public DisruptorEventLoggerThreadFactory() {
   }

   public Thread newThread(Runnable r) {
      Thread t = new Thread(r);
      t.setName("kmc-disruptor-event-logger-threadpool");
      t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
         {
            Objects.requireNonNull(DisruptorEventLoggerThreadFactory.this);
         }

         public void uncaughtException(Thread t, Throwable e) {
            LoggerFactory.getLogger(t.getName()).error(e.getMessage(), e);
         }
      });
      return t;
   }
}
