package com.kuma.boot.monitor.collect.task;

import com.kuma.boot.core.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.autoconfigure.properties.AsyncProperties;
import com.kuma.boot.core.support.Collector;
import com.kuma.boot.monitor.annotation.FieldReport;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.collect.AbstractCollectTask;
import com.kuma.boot.monitor.collect.CollectInfo;
import java.util.Objects;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class AsyncThreadPoolCollectTask extends AbstractCollectTask {
   private static final String TASK_NAME = "kmc.monitor.collect.executor.async";
   private final CollectTaskProperties properties;
   private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

   public AsyncThreadPoolCollectTask(CollectTaskProperties properties, ThreadPoolTaskExecutor threadPoolTaskExecutor) {
      this.properties = properties;
      this.threadPoolTaskExecutor = threadPoolTaskExecutor;
   }

   public int getTimeSpan() {
      return this.properties.getAsyncThreadTimeSpan();
   }

   public String getDesc() {
      return this.getClass().getName();
   }

   public String getName() {
      return "kmc.monitor.collect.executor.async";
   }

   public boolean getEnabled() {
      return this.properties.getAsyncThreadEnabled();
   }

   protected CollectInfo getData() {
      try {
         Collector collector = Collector.getCollector();
         AsyncProperties asyncProperties = (AsyncProperties)ContextUtils.getBean(AsyncProperties.class, true);
         if (Objects.nonNull(collector) && Objects.nonNull(asyncProperties)) {
            String asyncThreadName = asyncProperties.getThreadNamePrefix().replace("-", ".");
            AsyncExecutorCollectInfo info = new AsyncExecutorCollectInfo();
            Collector.Call var10001 = collector.call(asyncThreadName + ".active.count");
            ThreadPoolTaskExecutor var10002 = this.threadPoolTaskExecutor;
            Objects.requireNonNull(var10002);
            info.asyncExecutorActiveCount = (Integer)var10001.set(var10002::getActiveCount).run();
            var10001 = collector.call(asyncThreadName + ".core.pool.size");
            var10002 = this.threadPoolTaskExecutor;
            Objects.requireNonNull(var10002);
            info.asyncExecutorCorePoolSize = (Integer)var10001.set(var10002::getCorePoolSize).run();
            var10001 = collector.call(asyncThreadName + ".pool.size.largest");
            var10002 = this.threadPoolTaskExecutor;
            Objects.requireNonNull(var10002);
            info.asyncExecutorPoolSizeLargest = (Integer)var10001.set(var10002::getPoolSize).run();
            var10001 = collector.call(asyncThreadName + ".pool.size.max");
            var10002 = this.threadPoolTaskExecutor;
            Objects.requireNonNull(var10002);
            info.asyncExecutorPoolSizeMax = (Integer)var10001.set(var10002::getMaxPoolSize).run();
            var10001 = collector.call(asyncThreadName + ".pool.size.count");
            var10002 = this.threadPoolTaskExecutor;
            Objects.requireNonNull(var10002);
            info.asyncExecutorPoolSizeCount = (Integer)var10001.set(var10002::getCorePoolSize).run();
            var10001 = collector.call(asyncThreadName + ".queue.size");
            var10002 = this.threadPoolTaskExecutor;
            Objects.requireNonNull(var10002);
            info.asyncExecutorQueueSize = (Integer)var10001.set(var10002::getQueueSize).run();
            var10001 = collector.call(asyncThreadName + ".task.count");
            var10002 = this.threadPoolTaskExecutor;
            Objects.requireNonNull(var10002);
            info.asyncExecutorTaskCount = Long.valueOf(String.valueOf(var10001.set(var10002::getActiveCount).run()));
            var10001 = collector.call(asyncThreadName + ".task.completed");
            var10002 = this.threadPoolTaskExecutor;
            Objects.requireNonNull(var10002);
            info.asyncExecutorTaskCompleted = Long.valueOf(String.valueOf(var10001.set(var10002::getActiveCount).run()));
            Collector.Hook hook = collector.hook(asyncThreadName + ".hook");
            info.asyncExecutorTaskHookCurrent = hook.getCurrent();
            info.asyncExecutorTaskHookError = hook.getLastErrorPerSecond();
            info.asyncExecutorTaskHookSuccess = hook.getLastSuccessPerSecond();
            info.asyncExecutorTaskHookList = hook.getMaxTimeSpanList().toText();
            info.asyncExecutorTaskHookListPerMinute = hook.getMaxTimeSpanListPerMinute().toText();
            return info;
         }
      } catch (Exception e) {
         if (LogUtils.isErrorEnabled()) {
            LogUtils.error(e);
         }
      }

      return null;
   }

   private static class AsyncExecutorCollectInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.executor.async.active.count",
         desc = "异步核心线程池活动线程数"
      )
      private Integer asyncExecutorActiveCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.executor.async.core.pool.size",
         desc = "异步核心线程池核心线程数"
      )
      private Integer asyncExecutorCorePoolSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.executor.async.pool.size.largest",
         desc = "异步核心线程池历史最大线程数"
      )
      private Integer asyncExecutorPoolSizeLargest = 0;
      @FieldReport(
         name = "kmc.monitor.collect.executor.async.pool.size.max",
         desc = "异步核心线程池最大线程数"
      )
      private Integer asyncExecutorPoolSizeMax = 0;
      @FieldReport(
         name = "kmc.monitor.collect.executor.async.pool.size.count",
         desc = "异步核心线程池当前线程数"
      )
      private Integer asyncExecutorPoolSizeCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.executor.async.queue.size",
         desc = "异步核心线程池当前排队等待任务数"
      )
      private Integer asyncExecutorQueueSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.executor.async.task.count",
         desc = "异步核心线程池历史任务数"
      )
      private Long asyncExecutorTaskCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.executor.async.task.completed",
         desc = "异步核心线程池已完成任务数"
      )
      private Long asyncExecutorTaskCompleted = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.executor.async.task.hook.error",
         desc = "异步核心线程池拦截上一次每秒出错次数"
      )
      private Long asyncExecutorTaskHookError = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.executor.async.task.hook.success",
         desc = "异步核心线程池拦截上一次每秒成功次数"
      )
      private Long asyncExecutorTaskHookSuccess = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.executor.async.task.hook.current",
         desc = "异步核心线程池拦截当前执行任务数"
      )
      private Long asyncExecutorTaskHookCurrent = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.executor.async.task.hook.list.detail",
         desc = "异步核心线程池拦截历史最大耗时任务列表"
      )
      private String asyncExecutorTaskHookList = "";
      @FieldReport(
         name = "kmc.monitor.collect.executor.async.task.hook.list.minute.detail",
         desc = "异步核心线程池拦截历史最大耗时任务列表(每分钟)"
      )
      private String asyncExecutorTaskHookListPerMinute = "";

      private AsyncExecutorCollectInfo() {
      }
   }
}
