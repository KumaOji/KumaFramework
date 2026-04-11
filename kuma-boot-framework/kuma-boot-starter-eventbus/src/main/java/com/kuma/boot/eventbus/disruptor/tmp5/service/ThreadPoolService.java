package com.kuma.boot.eventbus.disruptor.tmp5.service;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.disruptor.tmp5.event.ThreadPoolExecutorErrorEvent;
import com.kuma.boot.eventbus.disruptor.tmp5.utils.ZlfDisruptorSpringUtils;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolService {
   private static final int DEFAULT_CORE_SIZE = 8;
   private static final int MAX_QUEUE_SIZE = 100;
   private static final int QUEUE_INIT_MAX_SIZE = 1000;
   private static volatile ThreadPoolExecutor executor;

   private ThreadPoolService() {
   }

   public static ThreadPoolExecutor getInstance() {
      if (executor == null) {
         synchronized(ThreadPoolService.class) {
            if (executor == null) {
               executor = new ThreadPoolExecutor(8, 100, 2147483647L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque(1000), Executors.defaultThreadFactory()) {
                  protected void afterExecute(Runnable r, Throwable t) {
                     if (t != null) {
                        LogUtils.error("afterExecute\u83b7\u53d6\u5230\u5f02\u5e38\u4fe1\u606f:{}", new Object[]{t.getMessage()});
                     }

                     if (r instanceof FutureTask) {
                        try {
                           FutureTask<?> futureTask = (FutureTask)r;
                           futureTask.get();
                        } catch (Exception e) {
                           LogUtils.error("afterExecute\u83b7\u53d6\u5230submit\u63d0\u4ea4\u7684\u5f02\u5e38\u4fe1\u606f:{}", new Object[]{e.getMessage()});
                        }
                     }

                     ThreadPoolExecutorErrorEvent threadPoolExecutorErrorEvent = new ThreadPoolExecutorErrorEvent(this, r, t);
                     ZlfDisruptorSpringUtils.getApplicationContext().publishEvent(threadPoolExecutorErrorEvent);
                     LogUtils.info("====ThreadPoolService.\u6355\u83b7\u5f02\u5e38\u53d1\u9001springBoot\u4e8b\u4ef6\u5b8c\u6210======", new Object[0]);
                  }
               };
               executor.setRejectedExecutionHandler((r, executor) -> {
                  try {
                     if (executor.isShutdown()) {
                        return;
                     }

                     executor.getQueue().put(r);
                  } catch (InterruptedException e) {
                     LogUtils.error("\u7ebf\u7a0b\u5904\u7406\u62d2\u7edd\u7b56\u7565\u5931\u8d25:{}", new Object[]{e.getMessage()});
                     e.printStackTrace();
                  }

               });
            }
         }
      }

      return executor;
   }
}
