package com.kuma.boot.monitor;

import cn.hutool.core.collection.CollUtil;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.thread.ThreadUtils;
import com.kuma.boot.core.autoconfigure.properties.AsyncProperties;
import com.kuma.boot.core.support.Collector;
import com.kuma.boot.core.support.Ref;
import com.kuma.boot.core.support.ShutdownHooks;
import com.kuma.boot.monitor.autoconfigure.properties.MonitorThreadPoolProperties;
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
import java.util.function.Function;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class Monitor {
   public static final String TTC_COLLECTOR_ASYNC_EXECUTOR_CALL_KEY = "kmc.async.executor";
   public static final String TTC_COLLECTOR_ASYNC_EXECUTOR_HOOK = "kmc.async.executor.hook";
   public static final String TTC_COLLECTOR_MONITOR_EXECUTOR_CALL_KEY = "kmc.monitor.executor";
   public static final String TTC_COLLECTOR_MONITOR_EXECUTOR_HOOK = "kmc.monitor.executor.hook";
   private ThreadPoolExecutor monitorThreadPoolExecutor;
   private ThreadPoolTaskExecutor asyncThreadPoolExecutor;
   private MonitorThreadPoolProperties monitorThreadPoolProperties;
   private AsyncProperties asyncProperties;
   private Collector collector;

   public Monitor(Collector collector, AsyncProperties asyncProperties, MonitorThreadPoolProperties monitorThreadPoolProperties, ThreadPoolTaskExecutor asyncThreadPoolExecutor, ThreadPoolExecutor monitorThreadPoolExecutor) {
      this.collector = collector;
      this.asyncThreadPoolExecutor = asyncThreadPoolExecutor;
      this.asyncProperties = asyncProperties;
      this.monitorThreadPoolExecutor = monitorThreadPoolExecutor;
      this.monitorThreadPoolProperties = monitorThreadPoolProperties;
      if (Objects.nonNull(this.monitorThreadPoolExecutor)) {
         Collector.Call var10000 = this.call("kmc.monitor.executor.active.count");
         ThreadPoolExecutor var10001 = this.monitorThreadPoolExecutor;
         Objects.requireNonNull(var10001);
         var10000.set(var10001::getActiveCount);
         var10000 = this.call("kmc.monitor.executor.core.pool.size");
         var10001 = this.monitorThreadPoolExecutor;
         Objects.requireNonNull(var10001);
         var10000.set(var10001::getCorePoolSize);
         var10000 = this.call("kmc.monitor.executor.pool.size.largest");
         var10001 = this.monitorThreadPoolExecutor;
         Objects.requireNonNull(var10001);
         var10000.set(var10001::getLargestPoolSize);
         var10000 = this.call("kmc.monitor.executor.pool.size.max");
         var10001 = this.monitorThreadPoolExecutor;
         Objects.requireNonNull(var10001);
         var10000.set(var10001::getMaximumPoolSize);
         var10000 = this.call("kmc.monitor.executor.pool.size.count");
         var10001 = this.monitorThreadPoolExecutor;
         Objects.requireNonNull(var10001);
         var10000.set(var10001::getPoolSize);
         this.call("kmc.monitor.executor.queue.size").set(() -> this.monitorThreadPoolExecutor.getQueue().size());
         var10000 = this.call("kmc.monitor.executor.task.count");
         var10001 = this.monitorThreadPoolExecutor;
         Objects.requireNonNull(var10001);
         var10000.set(var10001::getTaskCount);
         var10000 = this.call("kmc.monitor.executor.task.completed");
         var10001 = this.monitorThreadPoolExecutor;
         Objects.requireNonNull(var10001);
         var10000.set(var10001::getCompletedTaskCount);
      }

      if (Objects.nonNull(this.asyncThreadPoolExecutor)) {
         Collector.Call var12 = this.call("kmc.async.executor.active.count");
         ThreadPoolTaskExecutor var21 = this.asyncThreadPoolExecutor;
         Objects.requireNonNull(var21);
         var12.set(var21::getActiveCount);
         var12 = this.call("kmc.async.executor.core.pool.size");
         var21 = this.asyncThreadPoolExecutor;
         Objects.requireNonNull(var21);
         var12.set(var21::getCorePoolSize);
         this.call("kmc.async.executor.pool.size.largest").set(() -> this.asyncThreadPoolExecutor.getThreadPoolExecutor().getLargestPoolSize());
         this.call("kmc.async.executor.pool.size.max").set(() -> this.asyncThreadPoolExecutor.getThreadPoolExecutor().getMaximumPoolSize());
         var12 = this.call("kmc.async.executor.pool.size.count");
         var21 = this.asyncThreadPoolExecutor;
         Objects.requireNonNull(var21);
         var12.set(var21::getPoolSize);
         this.call("kmc.async.executor.queue.size").set(() -> this.asyncThreadPoolExecutor.getThreadPoolExecutor().getQueue().size());
         this.call("kmc.async.executor.task.count").set(() -> this.asyncThreadPoolExecutor.getThreadPoolExecutor().getTaskCount());
         this.call("kmc.async.executor.task.completed").set(() -> this.asyncThreadPoolExecutor.getThreadPoolExecutor().getCompletedTaskCount());
      }

      ShutdownHooks.register(new ShutdownHooks.ShutdownHookHandler() {
         {
            Objects.requireNonNull(Monitor.this);
         }

         public int getOrder() {
            return 1;
         }

         public void runHook() throws Exception {
            super.runHook();
            Monitor.this.monitorShutdown();
            Monitor.this.asyncShutdown();
         }

         public String description() {
            return "\u5173\u95ed\u76d1\u63a7\u73b0\u573a\u6c60\u3001\u5173\u95ed\u6838\u5fc3\u5f02\u6b65\u73b0\u573a\u6c60";
         }
      });
   }

   public Collector.Call call(String key) {
      return this.collector.call(key);
   }

   public Collector.Hook monitorHook() {
      return this.collector.hook("kmc.monitor.executor.hook");
   }

   public Collector.Hook asyncHook() {
      return this.collector.hook("kmc.async.executor.hook");
   }

   private void monitorThreadPoolCheckHealth() {
      if (this.monitorThreadPoolProperties.isCheckHealth() && this.monitorThreadPoolExecutor.getMaximumPoolSize() <= this.monitorThreadPoolExecutor.getPoolSize() && !this.monitorThreadPoolExecutor.getQueue().isEmpty()) {
         LogUtils.warn("\u76d1\u63a7\u7ebf\u7a0b\u6c60\u5df2\u6ee1 \u4efb\u52a1\u5f00\u59cb\u51fa\u73b0\u6392\u961f \u8bf7\u4fee\u6539\u914d\u7f6e [kuma.cloud.core.threadpool.monitor.maximumPoolSize] \u5f53\u524d\u6d3b\u52a8\u7ebf\u7a0b\u6570: {}", new Object[]{this.monitorThreadPoolExecutor.getActiveCount()});
      }

   }

   private void coreThreadPoolCheckHealth() {
      if (this.asyncProperties.isCheckHealth() && this.asyncThreadPoolExecutor.getMaxPoolSize() <= this.asyncThreadPoolExecutor.getPoolSize() && !this.asyncThreadPoolExecutor.getThreadPoolExecutor().getQueue().isEmpty()) {
         LogUtils.warn("\u6838\u5fc3\u7ebf\u7a0b\u6c60\u5df2\u6ee1 \u4efb\u52a1\u5f00\u59cb\u51fa\u73b0\u6392\u961f \u8bf7\u4fee\u6539\u914d\u7f6e [kuma.cloud.core.threadpool.async.threadPoolMaxSiz] \u5f53\u524d\u6d3b\u52a8\u7ebf\u7a0b\u6570: {}", new Object[]{this.asyncThreadPoolExecutor.getActiveCount()});
      }

   }

   public <T> Future<T> monitorSubmit(String taskName, Callable<T> task) {
      this.monitorThreadPoolCheckHealth();
      return (Future)this.monitorHook().run(taskName, () -> this.monitorThreadPoolExecutor.submit(task));
   }

   public <T> Future<T> asyncSubmit(String taskName, Callable<T> task) {
      if (Objects.isNull(this.asyncThreadPoolExecutor)) {
         LogUtils.warn("\u6838\u5fc3\u7ebf\u7a0b\u6c60\u672a\u521d\u59cb\u5316", new Object[0]);
         return null;
      } else {
         this.coreThreadPoolCheckHealth();
         return (Future)this.asyncHook().run(taskName, () -> this.asyncThreadPoolExecutor.submit(task));
      }
   }

   public void monitorSubmit(String taskName, Runnable task) {
      this.monitorThreadPoolCheckHealth();
      this.monitorHook().run(taskName, () -> this.monitorThreadPoolExecutor.submit(task));
   }

   public Future<?> asyncSubmit(String taskName, Runnable task) {
      if (Objects.isNull(this.asyncThreadPoolExecutor)) {
         LogUtils.warn("\u6838\u5fc3\u7ebf\u7a0b\u6c60\u672a\u521d\u59cb\u5316", new Object[0]);
         return null;
      } else {
         this.coreThreadPoolCheckHealth();
         return (Future)this.asyncHook().run(taskName, () -> this.asyncThreadPoolExecutor.submit(task));
      }
   }

   public boolean monitorIsShutdown() {
      return this.monitorThreadPoolExecutor.isShutdown();
   }

   public boolean coreIsShutdown() {
      if (Objects.isNull(this.asyncThreadPoolExecutor)) {
         LogUtils.warn("\u6838\u5fc3\u7ebf\u7a0b\u6c60\u672a\u521d\u59cb\u5316", new Object[0]);
         return true;
      } else {
         return this.asyncThreadPoolExecutor.getThreadPoolExecutor().isShutdown();
      }
   }

   public void monitorShutdown() {
      ThreadUtils.shutdownThreadPool(this.monitorThreadPoolExecutor);
   }

   public void asyncShutdown() {
      if (Objects.nonNull(this.asyncThreadPoolExecutor)) {
         this.asyncThreadPoolExecutor.destroy();
      }

   }

   public <T> void monitorParallelFor(String taskName, int parallelCount, List<T> array, final com.kuma.boot.common.model.Callable.Action1<T> action) {
      this.monitorThreadPoolCheckHealth();
      this.monitorHook().run(taskName, () -> {
         int parallelCount2 = parallelCount;
         if (parallelCount > array.size()) {
            parallelCount2 = array.size();
         }

         Queue<T> queueTasks = new LinkedList(array);

         while(!queueTasks.isEmpty()) {
            List<T> runningTasks = new ArrayList(parallelCount2);

            T task;
            for(int i = 0; i < parallelCount2 && (task = (T)queueTasks.poll()) != null; ++i) {
               runningTasks.add(task);
            }

            CountDownLatch latch = new CountDownLatch(runningTasks.size());
            List<Future<?>> result = new ArrayList(parallelCount2);

            for(T obj : runningTasks) {
               Future<?> future = this.monitorThreadPoolExecutor.submit(() -> {
                  try {
                     action.invoke(obj);
                  } finally {
                     latch.countDown();
                  }

               });
               result.add(future);
            }

            try {
               latch.await();
            } catch (InterruptedException exp) {
               LogUtils.error(exp, "parallelFor \u4efb\u52a1\u8ba1\u6570\u5f02\u5e38", new Object[0]);
            }

            for(Future<?> f : result) {
               try {
                  f.get();
               } catch (Exception exp) {
                  throw new BaseException("parallelFor\u5e76\u884c\u6267\u884c\u51fa\u9519", exp);
               }
            }
         }

         return 1;
      });
   }

   public <T> void monitorParallelFor2(String taskName, int parallelCount, Collection<T> array, final com.kuma.boot.common.model.Callable.Action1<T> action) {
      this.monitorThreadPoolCheckHealth();
      this.monitorHook().run(taskName, () -> {
         int parallelCount2 = parallelCount;
         if (parallelCount > array.size()) {
            parallelCount2 = array.size();
         }

         Queue<T> queueTasks = new LinkedList(array);
         if (!queueTasks.isEmpty()) {
            CountDownLatch latch = new CountDownLatch(parallelCount2);
            Object lock = new Object();
            Ref<Exception> exceptionRef = new Ref((Object)null);

            for(int i = 0; i < parallelCount2; ++i) {
               this.monitorThreadPoolExecutor.submit(() -> {
                  while(true) {
                     T task;
                     synchronized(lock) {
                        task = (T)queueTasks.poll();
                     }

                     if (task != null && exceptionRef.isNull()) {
                        try {
                           action.invoke(task);
                           continue;
                        } catch (Exception exp) {
                           latch.countDown();
                           exceptionRef.setData(exp);
                        }
                     } else {
                        latch.countDown();
                     }

                     return;
                  }
               });
            }

            try {
               latch.await();
            } catch (InterruptedException exp) {
               LogUtils.error(exp, "parallelFor \u4efb\u52a1\u8ba1\u6570\u5f02\u5e38", new Object[0]);
            }

            if (!exceptionRef.isNull()) {
               throw new BaseException("parallelFor \u5e76\u884c\u6267\u884c\u51fa\u9519", (Throwable)exceptionRef.getData());
            }
         }

         return 1;
      });
   }

   public <T> void parallelFor(String taskName, int parallelCount, Collection<T> taskList, final com.kuma.boot.common.model.Callable.Action1<T> action) {
      if (parallelCount < 2) {
         for(T t : taskList) {
            action.invoke(t);
         }
      } else {
         this.monitorParallelFor2(taskName, parallelCount, taskList, action);
      }

   }

   public <R, M, D> List<R> batchExecute(int batchExecuteSize, long timeout, List<D> dataList, Function<List<D>, M> middleFunc, Function<M, R> resultFunc) {
      int totalSize = dataList.size();
      int totalPage = totalSize / batchExecuteSize;
      ExecutorService pool = new ThreadPoolExecutor(totalPage + 1, totalPage + 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
      List<Future<M>> futureList = new ArrayList();

      for(int pageNum = 1; pageNum <= totalPage + 1; ++pageNum) {
         int starNum = (pageNum - 1) * batchExecuteSize;
         int endNum = Math.min(pageNum * batchExecuteSize, totalSize);
         List<D> temList = dataList.subList(starNum, endNum);
         if (CollUtil.isNotEmpty(temList)) {
            Callable<M> callable = () -> middleFunc.apply(temList);
            Future<M> future = pool.submit(callable);
            futureList.add(future);
         }
      }

      pool.shutdown();
      List<R> result = new ArrayList();

      try {
         boolean isFinish = pool.awaitTermination(timeout, TimeUnit.MINUTES);
         if (!isFinish) {
            pool.shutdownNow();
         }

         result = futureList.stream().map((ex) -> {
            try {
               return resultFunc.apply(ex.get());
            } catch (ExecutionException | InterruptedException ex) {
               throw new RuntimeException(ex);
            }
         }).toList();
      } catch (Exception e) {
         LogUtils.error(e);
      }

      return result;
   }

   public ThreadPoolExecutor getMonitorThreadPoolExecutor() {
      return this.monitorThreadPoolExecutor;
   }

   public void setMonitorThreadPoolExecutor(ThreadPoolExecutor monitorThreadPoolExecutor) {
      this.monitorThreadPoolExecutor = monitorThreadPoolExecutor;
   }

   public ThreadPoolTaskExecutor getAsyncThreadPoolExecutor() {
      return this.asyncThreadPoolExecutor;
   }

   public void setAsyncThreadPoolExecutor(ThreadPoolTaskExecutor asyncThreadPoolExecutor) {
      this.asyncThreadPoolExecutor = asyncThreadPoolExecutor;
   }

   public MonitorThreadPoolProperties getMonitorThreadPoolProperties() {
      return this.monitorThreadPoolProperties;
   }

   public void setMonitorThreadPoolProperties(MonitorThreadPoolProperties monitorThreadPoolProperties) {
      this.monitorThreadPoolProperties = monitorThreadPoolProperties;
   }

   public AsyncProperties getAsyncThreadPoolProperties() {
      return this.asyncProperties;
   }

   public void setAsyncThreadPoolProperties(AsyncProperties asyncProperties) {
      this.asyncProperties = asyncProperties;
   }

   public Collector getCollector() {
      return this.collector;
   }

   public void setCollector(Collector collector) {
      this.collector = collector;
   }
}
