/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.monitor;

import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.model.Callable.Action1;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.thread.ThreadUtils;
import com.kuma.boot.core.autoconfigure.properties.AsyncProperties;
import com.kuma.boot.core.support.Collector;
import com.kuma.boot.core.support.Ref;
import com.kuma.boot.core.support.ShutdownHooks;
import com.kuma.boot.monitor.autoconfigure.properties.MonitorThreadPoolProperties;
import cn.hutool.core.collection.CollUtil;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义线程池及关键方法包装实现 装饰
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:46:36
 */
public class Monitor {

   public static final String TTC_COLLECTOR_ASYNC_EXECUTOR_CALL_KEY = "ttc.async.executor";
   public static final String TTC_COLLECTOR_ASYNC_EXECUTOR_HOOK = "ttc.async.executor.hook";

   public static final String TTC_COLLECTOR_MONITOR_EXECUTOR_CALL_KEY = "ttc.monitor.executor";
   public static final String TTC_COLLECTOR_MONITOR_EXECUTOR_HOOK = "ttc.monitor.executor.hook";

   private ThreadPoolExecutor monitorThreadPoolExecutor;
   private ThreadPoolTaskExecutor asyncThreadPoolExecutor;

   private MonitorThreadPoolProperties monitorThreadPoolProperties;
   private AsyncProperties asyncProperties;

   private Collector collector;

   public Monitor(
           Collector collector,
           AsyncProperties asyncProperties,
           MonitorThreadPoolProperties monitorThreadPoolProperties,
           ThreadPoolTaskExecutor asyncThreadPoolExecutor,
           ThreadPoolExecutor monitorThreadPoolExecutor) {

      this.collector = collector;

      // 核心线程池
      this.asyncThreadPoolExecutor = asyncThreadPoolExecutor;
      this.asyncProperties = asyncProperties;
      // 监控线程池
      this.monitorThreadPoolExecutor = monitorThreadPoolExecutor;
      this.monitorThreadPoolProperties = monitorThreadPoolProperties;

      if (Objects.nonNull(this.monitorThreadPoolExecutor)) {
         call(TTC_COLLECTOR_MONITOR_EXECUTOR_CALL_KEY + ".active.count")
                 .set(this.monitorThreadPoolExecutor::getActiveCount);
         call(TTC_COLLECTOR_MONITOR_EXECUTOR_CALL_KEY + ".core.pool.size")
                 .set(this.monitorThreadPoolExecutor::getCorePoolSize);
         call(TTC_COLLECTOR_MONITOR_EXECUTOR_CALL_KEY + ".pool.size.largest")
                 .set(this.monitorThreadPoolExecutor::getLargestPoolSize);
         call(TTC_COLLECTOR_MONITOR_EXECUTOR_CALL_KEY + ".pool.size.max")
                 .set(this.monitorThreadPoolExecutor::getMaximumPoolSize);
         call(TTC_COLLECTOR_MONITOR_EXECUTOR_CALL_KEY + ".pool.size.count")
                 .set(this.monitorThreadPoolExecutor::getPoolSize);
         call(TTC_COLLECTOR_MONITOR_EXECUTOR_CALL_KEY + ".queue.size")
                 .set(() -> this.monitorThreadPoolExecutor.getQueue().size());
         call(TTC_COLLECTOR_MONITOR_EXECUTOR_CALL_KEY + ".task.count")
                 .set(this.monitorThreadPoolExecutor::getTaskCount);
         call(TTC_COLLECTOR_MONITOR_EXECUTOR_CALL_KEY + ".task.completed")
                 .set(this.monitorThreadPoolExecutor::getCompletedTaskCount);
      }

      if (Objects.nonNull(this.asyncThreadPoolExecutor)) {
         call(TTC_COLLECTOR_ASYNC_EXECUTOR_CALL_KEY + ".active.count")
                 .set(this.asyncThreadPoolExecutor::getActiveCount);
         call(TTC_COLLECTOR_ASYNC_EXECUTOR_CALL_KEY + ".core.pool.size")
                 .set(this.asyncThreadPoolExecutor::getCorePoolSize);
         call(TTC_COLLECTOR_ASYNC_EXECUTOR_CALL_KEY + ".pool.size.largest")
                 .set(() ->
                         this.asyncThreadPoolExecutor.getThreadPoolExecutor().getLargestPoolSize());
         call(TTC_COLLECTOR_ASYNC_EXECUTOR_CALL_KEY + ".pool.size.max")
                 .set(() ->
                         this.asyncThreadPoolExecutor.getThreadPoolExecutor().getMaximumPoolSize());
         call(TTC_COLLECTOR_ASYNC_EXECUTOR_CALL_KEY + ".pool.size.count")
                 .set(this.asyncThreadPoolExecutor::getPoolSize);
         call(TTC_COLLECTOR_ASYNC_EXECUTOR_CALL_KEY + ".queue.size").set(
                 () -> this.asyncThreadPoolExecutor
                         .getThreadPoolExecutor()
                         .getQueue()
                         .size());
         call(TTC_COLLECTOR_ASYNC_EXECUTOR_CALL_KEY + ".task.count")
                 .set(() ->
                         this.asyncThreadPoolExecutor.getThreadPoolExecutor().getTaskCount());
         call(TTC_COLLECTOR_ASYNC_EXECUTOR_CALL_KEY + ".task.completed")
                 .set(() ->
                         this.asyncThreadPoolExecutor.getThreadPoolExecutor().getCompletedTaskCount());
      }
      ShutdownHooks.register(new ShutdownHooks.ShutdownHookHandler() {

         @Override
         public int getOrder() {
            return 1;
         }

         @Override
         public void runHook() throws Exception {
            ShutdownHooks.ShutdownHookHandler.super.runHook();
            monitorShutdown();
            asyncShutdown();
         }

         @Override
         public String description() {
            return "关闭监控现场池、关闭核心异步现场池";
         }
      });
   }

   public Collector.Call call(String key) {
      return collector.call(key);
   }

   public Collector.Hook monitorHook() {
      return collector.hook(TTC_COLLECTOR_MONITOR_EXECUTOR_HOOK);
   }

   public Collector.Hook asyncHook() {
      return collector.hook(TTC_COLLECTOR_ASYNC_EXECUTOR_HOOK);
   }

   private void monitorThreadPoolCheckHealth() {
      if (monitorThreadPoolProperties.isCheckHealth()
              && monitorThreadPoolExecutor.getMaximumPoolSize()
              <= monitorThreadPoolExecutor.getPoolSize()
              && !monitorThreadPoolExecutor.getQueue().isEmpty()) {
         LogUtils.warn(
                 "监控线程池已满 任务开始出现排队 请修改配置 [kuma.cloud.core.threadpool.monitor.maximumPoolSize]"
                         + " 当前活动线程数: {}",
                 monitorThreadPoolExecutor.getActiveCount());
      }
   }

   private void coreThreadPoolCheckHealth() {
      if (asyncProperties.isCheckHealth()
              && asyncThreadPoolExecutor.getMaxPoolSize() <= asyncThreadPoolExecutor.getPoolSize()
              && !asyncThreadPoolExecutor.getThreadPoolExecutor().getQueue().isEmpty()) {
         LogUtils.warn(
                 "核心线程池已满 任务开始出现排队 请修改配置 [kuma.cloud.core.threadpool.async.threadPoolMaxSiz]"
                         + " 当前活动线程数: {}",
                 asyncThreadPoolExecutor.getActiveCount());
      }
   }

   public <T> Future<T> monitorSubmit(String taskName, Callable<T> task) {
      monitorThreadPoolCheckHealth();

      return monitorHook().run(taskName, () -> monitorThreadPoolExecutor.submit(task));
   }

   public <T> Future<T> asyncSubmit(String taskName, Callable<T> task) {
      if (Objects.isNull(asyncThreadPoolExecutor)) {
         LogUtils.warn("核心线程池未初始化");
         return null;
      }

      coreThreadPoolCheckHealth();
      return asyncHook().run(taskName, () -> asyncThreadPoolExecutor.submit(task));
   }

   public void monitorSubmit(String taskName, Runnable task) {
      monitorThreadPoolCheckHealth();
      monitorHook().run(taskName, () -> monitorThreadPoolExecutor.submit(task));
   }

   public Future<?> asyncSubmit(String taskName, Runnable task) {
      if (Objects.isNull(asyncThreadPoolExecutor)) {
         LogUtils.warn("核心线程池未初始化");
         return null;
      }

      coreThreadPoolCheckHealth();
      return asyncHook().run(taskName, () -> asyncThreadPoolExecutor.submit(task));
   }

   public boolean monitorIsShutdown() {
      return monitorThreadPoolExecutor.isShutdown();
   }

   public boolean coreIsShutdown() {
      if (Objects.isNull(asyncThreadPoolExecutor)) {
         LogUtils.warn("核心线程池未初始化");
         return true;
      }

      return asyncThreadPoolExecutor.getThreadPoolExecutor().isShutdown();
   }

   public void monitorShutdown() {
      ThreadUtils.shutdownThreadPool(monitorThreadPoolExecutor);
   }


   public void asyncShutdown() {
      if (Objects.nonNull(asyncThreadPoolExecutor)) {
         asyncThreadPoolExecutor.destroy();
      }
   }

   /**
    * 任务拆分多个小任务分批并行处理，并行处理完一批再并行处理下一批。 在抛出错误的时候有问题，未修复，仅试验，不要用这个方法。
    *
    * @param taskName      taskName
    * @param parallelCount parallelCount
    * @param array         array
    * @param action        action
    * @since 2021-09-02 20:48:09
    */
   public <T> void monitorParallelFor(String taskName, int parallelCount, List<T> array,
                                      final Action1<T> action) {
      monitorThreadPoolCheckHealth();

      monitorHook().run(taskName, () -> {
         int parallelCount2 = parallelCount;
         if (parallelCount2 > array.size()) {
            parallelCount2 = array.size();
         }

         // 任务队列
         Queue<T> queueTasks = new LinkedList<>(array);

         while (!queueTasks.isEmpty()) {
            // 运行任务列表
            final List<T> runningTasks = new ArrayList<>(parallelCount2);

            T task;

            for (int i = 0; i < parallelCount2; i++) {
               if ((task = queueTasks.poll()) != null) {
                  runningTasks.add(task);
               }
               else {
                  break;
               }
            }

            final CountDownLatch latch = new CountDownLatch(runningTasks.size());
            List<Future<?>> result = new ArrayList<>(parallelCount2);

            for (T obj : runningTasks) {
               Future<?> future = monitorThreadPoolExecutor.submit(() -> {
                  try {
                     action.invoke(obj);
                  }
                  finally {
                     latch.countDown();
                  }
               });
               result.add(future);
            }

            try {
               latch.await();
            }
            catch (InterruptedException exp) {
               LogUtils.error(exp, "parallelFor 任务计数异常");
            }

            for (Future<?> f : result) {
               try {
                  f.get();
               }
               catch (Exception exp) {
                  throw new BaseException("parallelFor并行执行出错", exp);
               }
            }
         }
         return 1;
      });
   }

   /**
    * 任务使用固定并行大小处理任务,直到所有任务处理完毕。
    *
    * @param taskName      taskName
    * @param parallelCount parallelCount
    * @param array         array
    * @param action        action
    * @since 2021-09-02 20:48:25
    */
   public <T> void monitorParallelFor2(
           String taskName, int parallelCount, Collection<T> array, final Action1<T> action) {
      monitorThreadPoolCheckHealth();
      monitorHook().run(taskName, () -> {
         int parallelCount2 = parallelCount;
         if (parallelCount2 > array.size()) {
            parallelCount2 = array.size();
         }
         // 任务队列
         Queue<T> queueTasks = new LinkedList<>(array);

         if (!queueTasks.isEmpty()) {
            final CountDownLatch latch = new CountDownLatch(parallelCount2);
            Object lock = new Object();
            Ref<Exception> exceptionRef = new Ref<>(null);
            for (int i = 0; i < parallelCount2; i++) {
               monitorThreadPoolExecutor.submit(() -> {
                  while (true) {
                     T task;
                     synchronized (lock) {
                        task = queueTasks.poll();
                     }

                     if (task != null && exceptionRef.isNull()) {
                        try {
                           action.invoke(task);
                        }
                        catch (Exception exp) {
                           latch.countDown();
                           exceptionRef.setData(exp);
                           break;
                        }
                     }
                     else {
                        latch.countDown();
                        break;
                     }
                  }
               });
            }

            try {
               latch.await();
            }
            catch (InterruptedException exp) {
               LogUtils.error(exp, "parallelFor 任务计数异常");
            }
            if (!exceptionRef.isNull()) {
               throw new BaseException("parallelFor 并行执行出错", exceptionRef.getData());
            }
         }
         return 1;
      });
   }


   /**
    * 使用系统线程池并行for循环
    *
    * @param taskName      任务名称
    * @param parallelCount 并行数量
    * @param taskList      任务列表
    * @param action        action
    * @since 2021-09-02 20:57:35
    */
   public <T> void parallelFor(
           String taskName, int parallelCount, Collection<T> taskList,
           final Action1<T> action) {
      if (parallelCount < 2) {
         for (T t : taskList) {
            action.invoke(t);
         }
      }
      else {
         monitorParallelFor2(taskName, parallelCount, taskList, action);
      }
   }


   /**
    * 线程池分批执行某个方法（batchExecute(temList,storeCode1)），所有数据都执行完成后返回数据
    *
    * @param batchExecuteSize 批次执行的数量
    * @param timeout          超时(分组)
    * @param dataList         数据列表
    * @param middleFunc       中间函数
    * @param resultFunc       结果函数
    * @return {@link List }<{@link R }>
    * @since 2022-09-19 16:35:32
    */
   public <R, M, D> List<R> batchExecute(
           int batchExecuteSize,
           long timeout,
           List<D> dataList,
           java.util.function.Function<List<D>, M> middleFunc,
           java.util.function.Function<M, R> resultFunc) {
      int totalSize = dataList.size();
      int totalPage = totalSize / batchExecuteSize;

      ExecutorService pool = new ThreadPoolExecutor(
              totalPage + 1, totalPage + 1, 0L, TimeUnit.MILLISECONDS,
              new LinkedBlockingQueue<Runnable>());

      List<Future<M>> futureList = new ArrayList<>();
      for (int pageNum = 1; pageNum <= totalPage + 1; pageNum++) {
         int starNum = (pageNum - 1) * batchExecuteSize;
         int endNum = Math.min(pageNum * batchExecuteSize, totalSize);
         List<D> temList = dataList.subList(starNum, endNum);

         if (CollUtil.isNotEmpty(temList)) {
            Callable<M> callable = () -> middleFunc.apply(temList);
            Future<M> future = pool.submit(callable);
            futureList.add(future);
         }
      }
      pool.shutdown(); // 不允许再想线程池中增加线程

      List<R> result = new ArrayList<>();
      try {
         // 判断是否所有线程已经执行完毕
         boolean isFinish = pool.awaitTermination(timeout, TimeUnit.MINUTES);
         // 如果没有执行完
         if (!isFinish) {
            // 线程池执行结束 不在等待线程执行完毕，直接执行下面的代码
            pool.shutdownNow();
         }

         result = futureList.stream()
                 .map(e -> {
                    try {
                       return resultFunc.apply(e.get());
                    }
                    catch (InterruptedException | ExecutionException ex) {
                       throw new RuntimeException(ex);
                    }
                 })
                 .toList();
      }
      catch (Exception e) {
         LogUtils.error(e);
      }
      return result;
   }

   public ThreadPoolExecutor getMonitorThreadPoolExecutor() {
      return monitorThreadPoolExecutor;
   }

   public void setMonitorThreadPoolExecutor(ThreadPoolExecutor monitorThreadPoolExecutor) {
      this.monitorThreadPoolExecutor = monitorThreadPoolExecutor;
   }

   public ThreadPoolTaskExecutor getAsyncThreadPoolExecutor() {
      return asyncThreadPoolExecutor;
   }

   public void setAsyncThreadPoolExecutor(ThreadPoolTaskExecutor asyncThreadPoolExecutor) {
      this.asyncThreadPoolExecutor = asyncThreadPoolExecutor;
   }

   public MonitorThreadPoolProperties getMonitorThreadPoolProperties() {
      return monitorThreadPoolProperties;
   }

   public void setMonitorThreadPoolProperties(
           MonitorThreadPoolProperties monitorThreadPoolProperties) {
      this.monitorThreadPoolProperties = monitorThreadPoolProperties;
   }

   public AsyncProperties getAsyncThreadPoolProperties() {
      return asyncProperties;
   }

   public void setAsyncThreadPoolProperties(AsyncProperties asyncProperties) {
      this.asyncProperties = asyncProperties;
   }

   public Collector getCollector() {
      return collector;
   }

   public void setCollector(Collector collector) {
      this.collector = collector;
   }
}
