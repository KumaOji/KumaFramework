package com.kuma.boot.monitor.collect.task;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.annotation.FieldReport;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.collect.AbstractCollectTask;
import com.kuma.boot.monitor.collect.CollectInfo;

public class HttpPoolCollectTask extends AbstractCollectTask {
   private static final String TASK_NAME = "kmc.monitor.collect.httpPool";
   private final CollectTaskProperties properties;

   public HttpPoolCollectTask(CollectTaskProperties properties) {
      this.properties = properties;
   }

   public int getTimeSpan() {
      return this.properties.getHttpPoolTimeSpan();
   }

   public boolean getEnabled() {
      return this.properties.isHttpPoolEnabled();
   }

   public String getDesc() {
      return this.getClass().getName();
   }

   public String getName() {
      return "kmc.monitor.collect.httpPool";
   }

   protected CollectInfo getData() {
      try {
         HttpPoolInfo info = new HttpPoolInfo();
         return info;
      } catch (Exception e) {
         if (LogUtils.isErrorEnabled()) {
            LogUtils.error(e);
         }

         return null;
      }
   }

   private static class HttpPoolInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.httpPool.available",
         desc = "HttpPool\u53ef\u7528\u7684\u8fde\u63a5\u6570"
      )
      private Integer availableCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.httpPool.pending",
         desc = "HttpPool\u7b49\u5f85\u7684\u8fde\u63a5\u6570"
      )
      private Integer pendingCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.httpPool.leased",
         desc = "HttpPool\u4f7f\u7528\u4e2d\u7684\u8fde\u63a5\u6570"
      )
      private Integer leasedCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.httpPool.detail",
         desc = "HttpPool\u8be6\u60c5"
      )
      private String poolDetail = "";

      private HttpPoolInfo() {
      }
   }
}
