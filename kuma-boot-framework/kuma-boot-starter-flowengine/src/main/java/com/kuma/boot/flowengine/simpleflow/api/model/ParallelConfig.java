package com.kuma.boot.flowengine.simpleflow.api.model;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelConfig {
   private ExecutionMode mode;
   private int corePoolSize;
   private int maximumPoolSize;
   private long keepAliveTime;
   private TimeUnit timeUnit;
   private int queueCapacity;
   private String threadNamePrefix;
   private boolean allowCoreThreadTimeOut;
   private long awaitTerminationSeconds;

   public ParallelConfig() {
      this.mode = ParallelConfig.ExecutionMode.SEQUENTIAL;
      this.corePoolSize = Runtime.getRuntime().availableProcessors();
      this.maximumPoolSize = Runtime.getRuntime().availableProcessors() * 2;
      this.keepAliveTime = 60L;
      this.timeUnit = TimeUnit.SECONDS;
      this.queueCapacity = 100;
      this.threadNamePrefix = "simple-flow-";
      this.allowCoreThreadTimeOut = false;
      this.awaitTerminationSeconds = 60L;
   }

   public ExecutionMode getMode() {
      return this.mode;
   }

   public void setMode(ExecutionMode mode) {
      this.mode = mode;
   }

   public int getCorePoolSize() {
      return this.corePoolSize;
   }

   public void setCorePoolSize(int corePoolSize) {
      this.corePoolSize = corePoolSize;
   }

   public int getMaximumPoolSize() {
      return this.maximumPoolSize;
   }

   public void setMaximumPoolSize(int maximumPoolSize) {
      this.maximumPoolSize = maximumPoolSize;
   }

   public long getKeepAliveTime() {
      return this.keepAliveTime;
   }

   public void setKeepAliveTime(long keepAliveTime) {
      this.keepAliveTime = keepAliveTime;
   }

   public TimeUnit getTimeUnit() {
      return this.timeUnit;
   }

   public void setTimeUnit(TimeUnit timeUnit) {
      this.timeUnit = timeUnit;
   }

   public int getQueueCapacity() {
      return this.queueCapacity;
   }

   public void setQueueCapacity(int queueCapacity) {
      this.queueCapacity = queueCapacity;
   }

   public String getThreadNamePrefix() {
      return this.threadNamePrefix;
   }

   public void setThreadNamePrefix(String threadNamePrefix) {
      this.threadNamePrefix = threadNamePrefix;
   }

   public boolean isAllowCoreThreadTimeOut() {
      return this.allowCoreThreadTimeOut;
   }

   public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
      this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
   }

   public long getAwaitTerminationSeconds() {
      return this.awaitTerminationSeconds;
   }

   public void setAwaitTerminationSeconds(long awaitTerminationSeconds) {
      this.awaitTerminationSeconds = awaitTerminationSeconds;
   }

   public ThreadPoolExecutor createThreadPoolExecutor() {
      ThreadPoolExecutor executor = new ThreadPoolExecutor(this.corePoolSize, this.maximumPoolSize, this.keepAliveTime, this.timeUnit, new LinkedBlockingQueue(this.queueCapacity), new SimpleFlowThreadFactory(this.threadNamePrefix));
      executor.allowCoreThreadTimeOut(this.allowCoreThreadTimeOut);
      return executor;
   }

   public static ParallelConfig sequential() {
      return builder().mode(ParallelConfig.ExecutionMode.SEQUENTIAL).build();
   }

   public static ParallelConfig parallel() {
      return builder().mode(ParallelConfig.ExecutionMode.PARALLEL).build();
   }

   public static ParallelConfigBuilder builder() {
      return new ParallelConfigBuilder();
   }

   public static enum ExecutionMode {
      SEQUENTIAL,
      PARALLEL;

      private ExecutionMode() {
      }

      // $FF: synthetic method
      private static ExecutionMode[] $values() {
         return new ExecutionMode[]{SEQUENTIAL, PARALLEL};
      }
   }

   private static class SimpleFlowThreadFactory implements ThreadFactory {
      private final AtomicInteger threadNumber = new AtomicInteger(1);
      private final String namePrefix;

      SimpleFlowThreadFactory(String namePrefix) {
         this.namePrefix = namePrefix;
      }

      public Thread newThread(Runnable r) {
         String var10003 = this.namePrefix;
         Thread t = new Thread(r, var10003 + this.threadNumber.getAndIncrement());
         if (t.isDaemon()) {
            t.setDaemon(false);
         }

         if (t.getPriority() != 5) {
            t.setPriority(5);
         }

         return t;
      }
   }

   public static final class ParallelConfigBuilder {
      private ExecutionMode mode;
      private int corePoolSize;
      private int maximumPoolSize;
      private long keepAliveTime;
      private TimeUnit timeUnit;
      private int queueCapacity;
      private String threadNamePrefix;
      private boolean allowCoreThreadTimeOut;
      private long awaitTerminationSeconds;

      private ParallelConfigBuilder() {
      }

      public ParallelConfigBuilder mode(ExecutionMode mode) {
         this.mode = mode;
         return this;
      }

      public ParallelConfigBuilder corePoolSize(int corePoolSize) {
         this.corePoolSize = corePoolSize;
         return this;
      }

      public ParallelConfigBuilder maximumPoolSize(int maximumPoolSize) {
         this.maximumPoolSize = maximumPoolSize;
         return this;
      }

      public ParallelConfigBuilder keepAliveTime(long keepAliveTime) {
         this.keepAliveTime = keepAliveTime;
         return this;
      }

      public ParallelConfigBuilder timeUnit(TimeUnit timeUnit) {
         this.timeUnit = timeUnit;
         return this;
      }

      public ParallelConfigBuilder queueCapacity(int queueCapacity) {
         this.queueCapacity = queueCapacity;
         return this;
      }

      public ParallelConfigBuilder threadNamePrefix(String threadNamePrefix) {
         this.threadNamePrefix = threadNamePrefix;
         return this;
      }

      public ParallelConfigBuilder allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
         this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
         return this;
      }

      public ParallelConfigBuilder awaitTerminationSeconds(long awaitTerminationSeconds) {
         this.awaitTerminationSeconds = awaitTerminationSeconds;
         return this;
      }

      public ParallelConfig build() {
         ParallelConfig parallelConfig = new ParallelConfig();
         parallelConfig.setMode(this.mode);
         parallelConfig.setCorePoolSize(this.corePoolSize);
         parallelConfig.setMaximumPoolSize(this.maximumPoolSize);
         parallelConfig.setKeepAliveTime(this.keepAliveTime);
         parallelConfig.setTimeUnit(this.timeUnit);
         parallelConfig.setQueueCapacity(this.queueCapacity);
         parallelConfig.setThreadNamePrefix(this.threadNamePrefix);
         parallelConfig.setAllowCoreThreadTimeOut(this.allowCoreThreadTimeOut);
         parallelConfig.setAwaitTerminationSeconds(this.awaitTerminationSeconds);
         return parallelConfig;
      }
   }
}
