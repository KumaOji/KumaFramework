package com.kuma.boot.eventbus.disruptor.tmp4.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SafeThreadPool {
   private final Semaphore semaphore;
   private final ThreadPoolExecutor threadPoolExecutor;

   public SafeThreadPool(String name, int permits, int keepAliveTime) {
      this.semaphore = new Semaphore(permits);
      this.threadPoolExecutor = new ThreadPoolExecutor(permits, permits * 2, (long)keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue(), NamedDaemonThreadFactory.getInstance(name));
   }

   public int getActiveCount() {
      return this.threadPoolExecutor.getActiveCount();
   }

   public void submit(Runnable task) {
      this.semaphore.acquireUninterruptibly();
      this.threadPoolExecutor.submit(() -> {
         try {
            task.run();
         } finally {
            this.semaphore.release();
         }

      });
   }

   public void shotDown() {
      this.threadPoolExecutor.shutdown();
   }
}
