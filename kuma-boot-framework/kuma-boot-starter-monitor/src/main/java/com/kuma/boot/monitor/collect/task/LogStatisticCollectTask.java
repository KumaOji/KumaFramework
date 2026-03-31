package com.kuma.boot.monitor.collect.task;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.support.Collector;
import com.kuma.boot.monitor.annotation.FieldReport;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.collect.AbstractCollectTask;
import com.kuma.boot.monitor.collect.CollectInfo;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class LogStatisticCollectTask extends AbstractCollectTask {
   private static final String TASK_NAME = "kmc.monitor.collect.log.statistic";
   private final CollectTaskProperties properties;

   public LogStatisticCollectTask(CollectTaskProperties properties) {
      this.properties = properties;
   }

   public int getTimeSpan() {
      return this.properties.getLogStatisticTimeSpan();
   }

   public boolean getEnabled() {
      return this.properties.isLogStatisticEnabled();
   }

   public String getDesc() {
      return this.getClass().getName();
   }

   public String getName() {
      return "kmc.monitor.collect.log.statistic";
   }

   protected CollectInfo getData() {
      try {
         LogErrorInfo info = new LogErrorInfo();
         Collector collector = Collector.getCollector();
         if (Objects.nonNull(collector)) {
            info.logerrorCount = collector.value("kmc.monitor.collect.log.error.count").get() == null ? 0 : ((AtomicLong)collector.value("kmc.monitor.collect.log.error.count").get()).intValue();
            info.logIncreCount = collector.value("kmc.monitor.collect.log.incre.count").get() == null ? 0 : ((AtomicLong)collector.value("kmc.monitor.collect.log.incre.count").get()).intValue();
            return info;
         }
      } catch (Exception e) {
         if (LogUtils.isErrorEnabled()) {
            LogUtils.error(e);
         }
      }

      return null;
   }

   private static class LogErrorInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.log.statistic.error.count",
         desc = "\u6700\u8fd11\u5206\u949f\u9519\u8bef\u65e5\u5fd7\u6570\u91cf"
      )
      private Integer logerrorCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.log.statistic.incre.count",
         desc = "\u6700\u8fd11\u5206\u949f\u65e5\u5fd7\u6761\u6570\u589e\u91cf"
      )
      private Integer logIncreCount = 0;

      private LogErrorInfo() {
      }
   }
}
