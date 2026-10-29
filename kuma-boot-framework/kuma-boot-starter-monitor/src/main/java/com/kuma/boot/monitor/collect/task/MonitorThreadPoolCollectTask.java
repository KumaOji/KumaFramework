package com.kuma.boot.monitor.collect.task;

import com.kuma.boot.core.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.support.Collector;
import com.kuma.boot.monitor.annotation.FieldReport;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.autoconfigure.properties.MonitorThreadPoolProperties;
import com.kuma.boot.monitor.collect.AbstractCollectTask;
import com.kuma.boot.monitor.collect.CollectInfo;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

public class MonitorThreadPoolCollectTask extends AbstractCollectTask {
   private static final String TASK_NAME = "kmc.monitor.collect.executor.monitor";
   private final CollectTaskProperties properties;
   private final ThreadPoolExecutor threadPoolExecutor;

   public MonitorThreadPoolCollectTask(CollectTaskProperties properties, ThreadPoolExecutor threadPoolExecutor) {
      this.properties = properties;
      this.threadPoolExecutor = threadPoolExecutor;
   }

   public int getTimeSpan() {
      return this.properties.getThreadPollTimeSpan();
   }

   public String getDesc() {
      return this.getClass().getName();
   }

   public String getName() {
      return "kmc.monitor.collect.executor.monitor";
   }

   public boolean getEnabled() {
      return this.properties.isThreadPollEnabled();
   }

   protected CollectInfo getData() {
      try {
         Collector collector = Collector.getCollector();
         MonitorThreadPoolProperties monitorThreadPoolProperties = (MonitorThreadPoolProperties)ContextUtils.getBean(MonitorThreadPoolProperties.class, false);
         if (Objects.nonNull(collector) && Objects.nonNull(monitorThreadPoolProperties)) {
            String threadNamePrefix = monitorThreadPoolProperties.getThreadNamePrefix();
            String monitorThreadName = threadNamePrefix.replace("-", ".");
            MonitorThreadPoolInfo info = new MonitorThreadPoolInfo();
            Collector.Call var10001 = collector.call(monitorThreadName + ".active.count");
            ThreadPoolExecutor var10002 = this.threadPoolExecutor;
            Objects.requireNonNull(var10002);
            info.systemActiveCount = (Integer)var10001.set(var10002::getActiveCount).run();
            var10001 = collector.call(monitorThreadName + ".core.pool.size");
            var10002 = this.threadPoolExecutor;
            Objects.requireNonNull(var10002);
            info.systemCorePoolSize = (Integer)var10001.set(var10002::getCorePoolSize).run();
            var10001 = collector.call(monitorThreadName + ".pool.size.largest");
            var10002 = this.threadPoolExecutor;
            Objects.requireNonNull(var10002);
            info.systemPoolSizeLargest = (Integer)var10001.set(var10002::getLargestPoolSize).run();
            var10001 = collector.call(monitorThreadName + ".pool.size.max");
            var10002 = this.threadPoolExecutor;
            Objects.requireNonNull(var10002);
            info.systemPoolSizeMax = (Integer)var10001.set(var10002::getMaximumPoolSize).run();
            var10001 = collector.call(monitorThreadName + ".pool.size.count");
            var10002 = this.threadPoolExecutor;
            Objects.requireNonNull(var10002);
            info.systemPoolSizeCount = (Integer)var10001.set(var10002::getPoolSize).run();
            info.systemQueueSize = (Integer)collector.call(monitorThreadName + ".queue.size").set(() -> this.threadPoolExecutor.getQueue().size()).run();
            var10001 = collector.call(monitorThreadName + ".task.count");
            var10002 = this.threadPoolExecutor;
            Objects.requireNonNull(var10002);
            info.systemTaskCount = (Long)var10001.set(var10002::getTaskCount).run();
            var10001 = collector.call(monitorThreadName + ".task.completed");
            var10002 = this.threadPoolExecutor;
            Objects.requireNonNull(var10002);
            info.systemTaskCompleted = (Long)var10001.set(var10002::getCompletedTaskCount).run();
            Collector.Hook hook = collector.hook(monitorThreadName + ".hook");
            info.systemTaskHookCurrent = hook.getCurrent();
            info.systemTaskHookError = hook.getLastErrorPerSecond();
            info.systemTaskHookSuccess = hook.getLastSuccessPerSecond();
            info.systemTaskHookList = hook.getMaxTimeSpanList().toText();
            info.systemTaskHookListPerMinute = hook.getMaxTimeSpanListPerMinute().toText();
            return info;
         }
      } catch (Exception e) {
         if (LogUtils.isErrorEnabled()) {
            LogUtils.error(e);
         }
      }

      return null;
   }

   private static class MonitorThreadPoolInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.active.count",
         desc = "系统线程池活动线程数"
      )
      private Integer systemActiveCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.core.pool.size",
         desc = "系统线程池核心线程数"
      )
      private Integer systemCorePoolSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.pool.size.largest",
         desc = "线程池历史最大线程数"
      )
      private Integer systemPoolSizeLargest = 0;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.pool.size.max",
         desc = "线程池最大线程数"
      )
      private Integer systemPoolSizeMax = 0;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.pool.size.count",
         desc = "线程池当前线程数"
      )
      private Integer systemPoolSizeCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.queue.size",
         desc = "线程池当前排队等待任务数"
      )
      private Integer systemQueueSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.task.count",
         desc = "线程池历史任务数"
      )
      private Long systemTaskCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.task.completed",
         desc = "线程池已完成任务数"
      )
      private Long systemTaskCompleted = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.task.hook.error",
         desc = "线程池拦截上一次每秒出错次数"
      )
      private Long systemTaskHookError = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.task.hook.success",
         desc = "线程池拦截上一次每秒成功次数"
      )
      private Long systemTaskHookSuccess = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.task.hook.current",
         desc = "线程池拦截当前执行任务数"
      )
      private Long systemTaskHookCurrent = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.task.hook.list.detail",
         desc = "线程池拦截历史最大耗时任务列表"
      )
      private String systemTaskHookList = "";
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.task.hook.list.minute.detail",
         desc = "线程池拦截历史最大耗时任务列表(每分钟)"
      )
      private String systemTaskHookListPerMinute = "";

      private MonitorThreadPoolInfo() {
      }
   }
}
