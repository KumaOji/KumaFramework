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
                        LogUtils.error("afterExecute获取到异常信息:{}", new Object[]{t.getMessage()});
                     }

                     if (r instanceof FutureTask) {
                        try {
                           FutureTask<?> futureTask = (FutureTask)r;
                           futureTask.get();
                        } catch (Exception e) {
                           LogUtils.error("afterExecute获取到submit提交的异常信息:{}", new Object[]{e.getMessage()});
                        }
                     }

                     ThreadPoolExecutorErrorEvent threadPoolExecutorErrorEvent = new ThreadPoolExecutorErrorEvent(this, r, t);
                     ZlfDisruptorSpringUtils.getApplicationContext().publishEvent(threadPoolExecutorErrorEvent);
                     LogUtils.info("====ThreadPoolService.捕获异常发送springBoot事件完成======", new Object[0]);
                  }
               };
               executor.setRejectedExecutionHandler((r, executor) -> {
                  try {
                     if (executor.isShutdown()) {
                        return;
                     }

                     executor.getQueue().put(r);
                  } catch (InterruptedException e) {
                     LogUtils.error("线程处理拒绝策略失败:{}", new Object[]{e.getMessage()});
                     e.printStackTrace();
                  }

               });
            }
         }
      }

      return executor;
   }
}
