package com.kuma.boot.eventbus.greenrobot;

import com.alibaba.ttl.threadpool.TtlExecutors;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

public class GreenrobotEventBusExecutor {
   private final GreenrobotEventBusExecutorProperties executorProperites;

   public GreenrobotEventBusExecutor(GreenrobotEventBusExecutorProperties executorProperites) {
      this.executorProperites = executorProperites;
   }

   public ExecutorService getThreadPoolTaskExecutor() {
      ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 5L, TimeUnit.MINUTES, new LinkedBlockingQueue(1000), new ThreadPoolExecutor.CallerRunsPolicy());
      return TtlExecutors.getTtlExecutorService(executor);
   }

   static class MDCTaskDecorator implements TaskDecorator {
      MDCTaskDecorator() {
      }

      public Runnable decorate(Runnable runnable) {
         Map<String, String> contextMap = MDC.getCopyOfContextMap();
         return () -> {
            if (contextMap != null) {
               MDC.setContextMap(contextMap);
            }

            runnable.run();
         };
      }
   }
}
