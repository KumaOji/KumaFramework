package com.kuma.boot.monitor.collect.task;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.support.Collector;
import com.kuma.boot.monitor.annotation.FieldReport;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.collect.AbstractCollectTask;
import com.kuma.boot.monitor.collect.CollectInfo;
import java.util.Objects;

public class DoubtApiCollectTask extends AbstractCollectTask {
   private static final String TASK_NAME = "kmc.monitor.collect.doubtApi";
   private final CollectTaskProperties properties;

   public DoubtApiCollectTask(CollectTaskProperties properties) {
      this.properties = properties;
   }

   public int getTimeSpan() {
      return this.properties.getDoubtApiTimeSpan();
   }

   public boolean getEnabled() {
      return this.properties.isDoubtApiEnabled();
   }

   public String getDesc() {
      return this.getClass().getName();
   }

   public String getName() {
      return "kmc.monitor.collect.doubtApi";
   }

   protected CollectInfo getData() {
      try {
         DoubtApiInfo info = new DoubtApiInfo();
         Collector collector = Collector.getCollector();
         if (Objects.nonNull(collector)) {
            Object doubtApiInfo = collector.value("kmc.monitor.doubtapi.info").get();
            return info;
         }
      } catch (Exception e) {
         if (LogUtils.isErrorEnabled()) {
            LogUtils.error(e);
         }
      }

      return null;
   }

   public static class DoubtApiInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.doubtApi.detail",
         desc = "可疑内存增长api分析报告"
      )
      String detail = "";

      public DoubtApiInfo() {
      }
   }
}
