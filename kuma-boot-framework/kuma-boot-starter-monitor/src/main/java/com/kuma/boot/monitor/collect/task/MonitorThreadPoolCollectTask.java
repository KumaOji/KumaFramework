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
         desc = "\u7cfb\u7edf\u7ebf\u7a0b\u6c60\u6d3b\u52a8\u7ebf\u7a0b\u6570"
      )
      private Integer systemActiveCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.core.pool.size",
         desc = "\u7cfb\u7edf\u7ebf\u7a0b\u6c60\u6838\u5fc3\u7ebf\u7a0b\u6570"
      )
      private Integer systemCorePoolSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.pool.size.largest",
         desc = "\u7ebf\u7a0b\u6c60\u5386\u53f2\u6700\u5927\u7ebf\u7a0b\u6570"
      )
      private Integer systemPoolSizeLargest = 0;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.pool.size.max",
         desc = "\u7ebf\u7a0b\u6c60\u6700\u5927\u7ebf\u7a0b\u6570"
      )
      private Integer systemPoolSizeMax = 0;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.pool.size.count",
         desc = "\u7ebf\u7a0b\u6c60\u5f53\u524d\u7ebf\u7a0b\u6570"
      )
      private Integer systemPoolSizeCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.queue.size",
         desc = "\u7ebf\u7a0b\u6c60\u5f53\u524d\u6392\u961f\u7b49\u5f85\u4efb\u52a1\u6570"
      )
      private Integer systemQueueSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.task.count",
         desc = "\u7ebf\u7a0b\u6c60\u5386\u53f2\u4efb\u52a1\u6570"
      )
      private Long systemTaskCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.task.completed",
         desc = "\u7ebf\u7a0b\u6c60\u5df2\u5b8c\u6210\u4efb\u52a1\u6570"
      )
      private Long systemTaskCompleted = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.task.hook.error",
         desc = "\u7ebf\u7a0b\u6c60\u62e6\u622a\u4e0a\u4e00\u6b21\u6bcf\u79d2\u51fa\u9519\u6b21\u6570"
      )
      private Long systemTaskHookError = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.task.hook.success",
         desc = "\u7ebf\u7a0b\u6c60\u62e6\u622a\u4e0a\u4e00\u6b21\u6bcf\u79d2\u6210\u529f\u6b21\u6570"
      )
      private Long systemTaskHookSuccess = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.task.hook.current",
         desc = "\u7ebf\u7a0b\u6c60\u62e6\u622a\u5f53\u524d\u6267\u884c\u4efb\u52a1\u6570"
      )
      private Long systemTaskHookCurrent = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.task.hook.list.detail",
         desc = "\u7ebf\u7a0b\u6c60\u62e6\u622a\u5386\u53f2\u6700\u5927\u8017\u65f6\u4efb\u52a1\u5217\u8868"
      )
      private String systemTaskHookList = "";
      @FieldReport(
         name = "kmc.monitor.collect.executor.monitor.task.hook.list.minute.detail",
         desc = "\u7ebf\u7a0b\u6c60\u62e6\u622a\u5386\u53f2\u6700\u5927\u8017\u65f6\u4efb\u52a1\u5217\u8868(\u6bcf\u5206\u949f)"
      )
      private String systemTaskHookListPerMinute = "";

      private MonitorThreadPoolInfo() {
      }
   }
}
