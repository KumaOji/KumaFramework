/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.eventbus.event.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.*;

/**
 * 线程池配置
 */
@AutoConfiguration
public class TaskExecuterConfig {

   /**
    * 线程池配置
    *
    * @return
    */
   @Bean(name = "event-task-pool-executor")
   public ExecutorService executorService() {
      // Java虚拟机的可用的处理器数量
      int numCores = Runtime.getRuntime().availableProcessors();
      // 核心线程数
      int coolSize = numCores;
      // 最大线程数
      int maxSize = numCores * 2;
      // 超时时间
      int keepAlive = 60;
      // 任务队列
      int queueNum = numCores;
      String taskNamePrefix = "event-task-pool-";
      // 默认 任务执行线程池大小 与 任务调度池程池大小
      // 任务队列
      BlockingQueue<Runnable> queue = numCores > 0 ? new LinkedBlockingQueue<>(numCores) : new SynchronousQueue<>();
      // 拒绝策略类型
      int handleType = 0;
      // 拒绝策略
      RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
      switch (handleType) {
         case 1:
            handler = new ThreadPoolExecutor.CallerRunsPolicy();
            break;
         case 2:
            handler = new ThreadPoolExecutor.DiscardOldestPolicy();
            break;
         case 3:
            handler = new ThreadPoolExecutor.DiscardPolicy();
            break;
         default:
            handler = new ThreadPoolExecutor.AbortPolicy();
      }
      return TtlExecutors.getTtlExecutorService(new ThreadPoolExecutor(
              numCores,
              numCores * 2,
              60,
              TimeUnit.SECONDS,
              queue,
              new ThreadFactoryBuilder().setNameFormat(taskNamePrefix).build(),
              handler));
   }

   /**
    * 配置异步事件总线
    *
    * @return 异步总线
    */
   @Bean
   @Primary
   public AsyncEventBus asyncEventBus() {
      return new AsyncEventBus(executorService());
   }

   /**
    * 配置同步事件总线
    *
    * @return 同步总线
    */
   @Bean
   public EventBus eventBus() {
      return new EventBus();
   }
}
