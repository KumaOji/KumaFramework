package com.kuma.boot.eventbus.event.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@AutoConfiguration
public class TaskExecuterConfig {
   public TaskExecuterConfig() {
   }

   @Bean(
      name = {"event-task-pool-executor"}
   )
   public ExecutorService executorService() {
      int numCores = Runtime.getRuntime().availableProcessors();
      int maxSize = numCores * 2;
      int keepAlive = 60;
      String taskNamePrefix = "event-task-pool-";
      BlockingQueue<Runnable> queue = (BlockingQueue<Runnable>)(numCores > 0 ? new LinkedBlockingQueue(numCores) : new SynchronousQueue());
      int handleType = 0;
      new ThreadPoolExecutor.AbortPolicy();
      Object handler;
      switch (handleType) {
         case 1 -> handler = new ThreadPoolExecutor.CallerRunsPolicy();
         case 2 -> handler = new ThreadPoolExecutor.DiscardOldestPolicy();
         case 3 -> handler = new ThreadPoolExecutor.DiscardPolicy();
         default -> handler = new ThreadPoolExecutor.AbortPolicy();
      }

      return TtlExecutors.getTtlExecutorService(new ThreadPoolExecutor(numCores, numCores * 2, 60L, TimeUnit.SECONDS, queue, (new ThreadFactoryBuilder()).setNameFormat(taskNamePrefix).build(), (RejectedExecutionHandler)handler));
   }

   @Bean
   @Primary
   public AsyncEventBus asyncEventBus() {
      return new AsyncEventBus(this.executorService());
   }

   @Bean
   public EventBus eventBus() {
      return new EventBus();
   }
}
