package com.kuma.boot.eventbus.atlas.core;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AdaptiveThreadPoolEventBus implements EventBus {
   private final EventBus delegate;
   private final ThreadPoolExecutor threadPool;
   private final ScheduledExecutorService monitorService;
   private final int minThreads;
   private final int maxThreads;
   private final double targetUtilization;
   private final long monitorInterval;

   public AdaptiveThreadPoolEventBus(EventBus delegate, int corePoolSize, int maxPoolSize, double targetUtilization, int monitorIntervalSeconds) {
      this.delegate = delegate;
      this.minThreads = corePoolSize;
      this.maxThreads = maxPoolSize;
      this.targetUtilization = targetUtilization;
      this.monitorInterval = TimeUnit.SECONDS.toMillis((long)monitorIntervalSeconds);
      this.threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(), new AdaptiveThreadFactory());
      this.threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
      this.monitorService = Executors.newSingleThreadScheduledExecutor();
      this.monitorService.scheduleAtFixedRate(this::adjustThreadPool, this.monitorInterval, this.monitorInterval, TimeUnit.MILLISECONDS);
   }

   public void publish(Event event) {
      this.threadPool.execute(() -> this.delegate.publish(event));
   }

   public void register(Object listener) {
      this.delegate.register(listener);
   }

   public void unregister(Object listener) {
      this.delegate.unregister(listener);
   }

   public void scanAndRegister(Object listener) {
      this.delegate.scanAndRegister(listener);
   }

   private void adjustThreadPool() {
      int activeThreads = this.threadPool.getActiveCount();
      int poolSize = this.threadPool.getPoolSize();
      int queueSize = this.threadPool.getQueue().size();
      double utilization = poolSize > 0 ? (double)activeThreads / (double)poolSize : (double)0.0F;
      if (utilization > this.targetUtilization && queueSize > 0 && poolSize < this.maxThreads) {
         int newCoreSize = Math.min(poolSize + 2, this.maxThreads);
         this.threadPool.setCorePoolSize(newCoreSize);
         System.out.println("Increasing thread pool size to: " + newCoreSize);
      } else if (utilization < this.targetUtilization * (double)0.5F && poolSize > this.minThreads) {
         int newCoreSize = Math.max(poolSize - 1, this.minThreads);
         this.threadPool.setCorePoolSize(newCoreSize);
         System.out.println("Decreasing thread pool size to: " + newCoreSize);
      }

   }

   public void shutdown() {
      this.monitorService.shutdown();
      this.threadPool.shutdown();

      try {
         if (!this.threadPool.awaitTermination(10L, TimeUnit.SECONDS)) {
            this.threadPool.shutdownNow();
         }
      } catch (InterruptedException var2) {
         Thread.currentThread().interrupt();
         this.threadPool.shutdownNow();
      }

   }

   private static class AdaptiveThreadFactory implements ThreadFactory {
      private static final AtomicInteger poolNumber = new AtomicInteger(1);
      private final ThreadGroup group;
      private final AtomicInteger threadNumber = new AtomicInteger(1);
      private final String namePrefix;

      AdaptiveThreadFactory() {
         SecurityManager s = System.getSecurityManager();
         this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
         this.namePrefix = "adaptive-event-processor-" + poolNumber.getAndIncrement() + "-";
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
