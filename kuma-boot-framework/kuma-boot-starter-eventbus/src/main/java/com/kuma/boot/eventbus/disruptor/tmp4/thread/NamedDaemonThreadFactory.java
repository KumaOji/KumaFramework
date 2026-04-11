package com.kuma.boot.eventbus.disruptor.tmp4.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.jspecify.annotations.NonNull;

public class NamedDaemonThreadFactory implements ThreadFactory {
   private final String name;
   private final AtomicInteger counter = new AtomicInteger(0);

   public NamedDaemonThreadFactory(String name) {
      this.name = name;
   }

   public static NamedDaemonThreadFactory getInstance(@NonNull String name) {
      return (NamedDaemonThreadFactory)NamedThreadFactoryManager.INSTANCE.acquire(name);
   }

   public Thread newThread(@NonNull Runnable r) {
      String var10003 = this.name;
      Thread thread = new Thread(r, var10003 + "-" + this.counter.incrementAndGet());
      thread.setDaemon(true);
      return thread;
   }
}
