package com.kuma.boot.eventbus.atlas.core;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncEventBus extends DefaultEventBus {
   private final ExecutorService executorService;

   public AsyncEventBus(int threadPoolSize) {
      this(Executors.newFixedThreadPool(threadPoolSize, new EventThreadFactory()));
   }

   public AsyncEventBus(ExecutorService executorService) {
      this.executorService = executorService;
   }

   public void publish(Event event) {
      this.executorService.submit(() -> super.publish(event));
   }

   public void shutdown() {
      this.executorService.shutdown();
   }

   public List<Runnable> shutdownNow() {
      return this.executorService.shutdownNow();
   }

   private static class EventThreadFactory implements ThreadFactory {
      private static final AtomicInteger poolNumber = new AtomicInteger(1);
      private final ThreadGroup group;
      private final AtomicInteger threadNumber = new AtomicInteger(1);
      private final String namePrefix;

      EventThreadFactory() {
         SecurityManager s = System.getSecurityManager();
         this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
         this.namePrefix = "event-bus-pool-" + poolNumber.getAndIncrement() + "-thread-";
      }

      public Thread newThread(Runnable r) {
         Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
         if (t.isDaemon()) {
            t.setDaemon(false);
         }

         if (t.getPriority() != 5) {
            t.setPriority(5);
         }

         return t;
      }
   }
}
