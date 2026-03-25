package com.kuma.boot.sms.common.executor;

import com.kuma.boot.sms.common.enums.RejectPolicy;
import com.kuma.boot.sms.common.properties.SmsAsyncProperties;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import org.jspecify.annotations.Nullable;

public abstract class AbstractSendAsyncThreadPoolExecutor implements SendAsyncThreadPoolExecutor {
   protected final SmsAsyncProperties config;

   public AbstractSendAsyncThreadPoolExecutor(SmsAsyncProperties config) {
      this.config = config;
   }

   protected static RejectedExecutionHandler buildRejectedExecutionHandler(@Nullable RejectPolicy type) {
      if (type == null) {
         return new ThreadPoolExecutor.AbortPolicy();
      } else {
         Object var10000;
         switch (type) {
            case Caller -> var10000 = new ThreadPoolExecutor.CallerRunsPolicy();
            case Discard -> var10000 = new ThreadPoolExecutor.DiscardPolicy();
            case DiscardOldest -> var10000 = new ThreadPoolExecutor.DiscardOldestPolicy();
            default -> var10000 = new ThreadPoolExecutor.AbortPolicy();
         }

         return (RejectedExecutionHandler)var10000;
      }
   }

   public final void submit(Runnable command) {
      this.submit0(command);
   }

   protected abstract void submit0(Runnable command);

   public static class DefaultThreadFactory implements ThreadFactory {
      private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
      private final ThreadGroup group = Thread.currentThread().getThreadGroup();
      private final AtomicInteger threadNumber = new AtomicInteger(1);
      private final String namePrefix;

      public DefaultThreadFactory() {
         this.namePrefix = "kmc-sms-async-executor-pool-" + POOL_NUMBER.getAndIncrement();
      }

      public Thread newThread(Runnable r) {
         Thread t = new Thread(this.group, r, this.namePrefix + "-thread-" + this.threadNumber.getAndIncrement(), 0L);
         t.setDaemon(false);
         if (t.getPriority() != 5) {
            t.setPriority(5);
         }

         return t;
      }

      public ThreadGroup getGroup() {
         return this.group;
      }

      public AtomicInteger getThreadNumber() {
         return this.threadNumber;
      }

      public String getNamePrefix() {
         return this.namePrefix;
      }
   }
}
