package com.kuma.boot.eventbus.disruptor.tmp5.factory;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.disruptor.tmp5.builder.ThreadPoolExecutorBuilder;
import com.kuma.boot.eventbus.disruptor.tmp5.enums.BlockingQueueTypeEnum;
import com.kuma.boot.eventbus.disruptor.tmp5.enums.RejectedPolicyTypeEnum;
import com.kuma.boot.eventbus.disruptor.tmp5.event.ThreadPoolExecutorErrorEvent;
import com.kuma.boot.eventbus.disruptor.tmp5.utils.ZlfDisruptorSpringUtils;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorFactory {
   private ThreadPoolExecutorBuilder threadPoolExecutorBuilder;

   public ThreadPoolExecutorFactory(ThreadPoolExecutorBuilder threadPoolExecutorBuilder) {
      this.threadPoolExecutorBuilder = threadPoolExecutorBuilder;
   }

   public ThreadPoolExecutor createThreadPoolExecutor() {
      BlockingQueue<Runnable> workQueue = null;
      RejectedExecutionHandler rejectedExecutionHandler = null;
      int defaultCoreSize = this.threadPoolExecutorBuilder.getDefaultCoreSize();
      if (Objects.isNull(defaultCoreSize) || defaultCoreSize <= 0) {
         defaultCoreSize = 8;
      }

      int maxQueueSize = this.threadPoolExecutorBuilder.getMaxQueueSize();
      if (Objects.isNull(maxQueueSize) || maxQueueSize <= 0) {
         maxQueueSize = 100;
      }

      long keepAliveTime = this.threadPoolExecutorBuilder.getKeepAliveTime();
      if (Objects.isNull(keepAliveTime) || keepAliveTime < 0L) {
         keepAliveTime = 2147483647L;
      }

      TimeUnit unit = this.threadPoolExecutorBuilder.getUnit();
      if (Objects.isNull(unit)) {
         unit = TimeUnit.MILLISECONDS;
      }

      int queueInitMaxSize = this.threadPoolExecutorBuilder.getQueueInitMaxSize();
      if (Objects.isNull(queueInitMaxSize) || queueInitMaxSize <= 0) {
         queueInitMaxSize = 1000;
      }

      BlockingQueueTypeEnum blockingQueueTypeEnum = this.threadPoolExecutorBuilder.getBlockingQueueTypeEnum();
      Object var13;
      if (Objects.isNull(blockingQueueTypeEnum)) {
         var13 = new LinkedBlockingDeque(queueInitMaxSize);
      } else {
         if (Objects.isNull(this.threadPoolExecutorBuilder.getBlockingQueueIsFair())) {
            this.threadPoolExecutorBuilder.setBlockingQueueIsFair(false);
         }

         var13 = BlockingQueueTypeEnum.buildBq(blockingQueueTypeEnum.getName(), queueInitMaxSize, this.threadPoolExecutorBuilder.getBlockingQueueIsFair());
      }

      ThreadFactory threadFactory = this.threadPoolExecutorBuilder.getThreadFactory();
      if (Objects.isNull(threadFactory)) {
         threadFactory = Executors.defaultThreadFactory();
      }

      RejectedPolicyTypeEnum rejectedPolicyTypeEnum = this.threadPoolExecutorBuilder.getRejectedPolicyTypeEnum();
      if (Objects.isNull(rejectedPolicyTypeEnum)) {
         rejectedExecutionHandler = (r, executor2) -> {
            try {
               if (executor2.isShutdown()) {
                  return;
               }

               executor2.getQueue().put(r);
            } catch (InterruptedException e) {
               LogUtils.error("\u7ebf\u7a0b\u5904\u7406\u62d2\u7edd\u7b56\u7565\u5931\u8d252:{}", new Object[]{e.getMessage()});
               e.printStackTrace();
            }

         };
      } else {
         rejectedExecutionHandler = RejectedPolicyTypeEnum.getRejectedPolicyTypeEnumByName(rejectedPolicyTypeEnum.getName());
      }

      ThreadPoolExecutor executor = new ThreadPoolExecutor(defaultCoreSize, maxQueueSize, keepAliveTime, unit, (BlockingQueue)var13, threadFactory) {
         {
            Objects.requireNonNull(ThreadPoolExecutorFactory.this);
         }

         protected void afterExecute(Runnable r, Throwable t) {
            if (t != null) {
               LogUtils.error("afterExecute\u83b7\u53d6\u5230\u5f02\u5e38\u4fe1\u606f2:{}", new Object[]{t.getMessage()});
            }

            if (r instanceof FutureTask) {
               try {
                  FutureTask<?> futureTask = (FutureTask)r;
                  futureTask.get();
               } catch (Exception e) {
                  LogUtils.error("afterExecute\u83b7\u53d6\u5230submit\u63d0\u4ea4\u7684\u5f02\u5e38\u4fe1\u606f2:{}", new Object[]{e.getMessage()});
               }
            }

            ThreadPoolExecutorErrorEvent threadPoolExecutorErrorEvent = new ThreadPoolExecutorErrorEvent(this, r, t);
            ZlfDisruptorSpringUtils.getApplicationContext().publishEvent(threadPoolExecutorErrorEvent);
            LogUtils.info("====ThreadPoolExecutorFactory.\u6355\u83b7\u5f02\u5e38\u53d1\u9001springBoot\u4e8b\u4ef6\u5b8c\u6210======", new Object[0]);
         }
      };
      executor.setRejectedExecutionHandler(rejectedExecutionHandler);
      return executor;
   }

   public ThreadPoolExecutorBuilder getThreadPoolExecutorBuilder() {
      return this.threadPoolExecutorBuilder;
   }

   public void setThreadPoolExecutorBuilder(ThreadPoolExecutorBuilder threadPoolExecutorBuilder) {
      this.threadPoolExecutorBuilder = threadPoolExecutorBuilder;
   }
}
