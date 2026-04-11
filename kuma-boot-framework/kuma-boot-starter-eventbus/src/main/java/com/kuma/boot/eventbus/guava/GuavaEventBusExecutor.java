package com.kuma.boot.eventbus.guava;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class GuavaEventBusExecutor {
   private final GuavaEventBusExecutorProperties executorProperites;

   public GuavaEventBusExecutor(GuavaEventBusExecutorProperties executorProperites) {
      this.executorProperites = executorProperites;
   }

   public Executor getThreadPoolTaskExecutor() {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(this.executorProperites.getCorePoolSize());
      executor.setMaxPoolSize(this.executorProperites.getMaxPoolSize());
      executor.setQueueCapacity(this.executorProperites.getQueueCapacity());
      executor.setThreadNamePrefix(this.executorProperites.getThreadNamePrefix());
      executor.setAllowCoreThreadTimeOut(this.executorProperites.isAllowCoreThreadTimeOut());
      executor.setKeepAliveSeconds(this.executorProperites.getKeepAliveSeconds());
      executor.setTaskDecorator(new MDCTaskDecorator());
      executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
      executor.setWaitForTasksToCompleteOnShutdown(true);
      executor.initialize();
      return executor;
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
