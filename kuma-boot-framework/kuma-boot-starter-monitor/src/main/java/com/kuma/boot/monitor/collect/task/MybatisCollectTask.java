package com.kuma.boot.monitor.collect.task;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.support.Collector;
import com.kuma.boot.monitor.annotation.FieldReport;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.collect.AbstractCollectTask;
import com.kuma.boot.monitor.collect.CollectInfo;
import java.util.Objects;

public class MybatisCollectTask extends AbstractCollectTask {
   private static final String TASK_NAME = "kmc.monitor.collect.mybatis";
   private final CollectTaskProperties properties;

   public MybatisCollectTask(CollectTaskProperties properties) {
      this.properties = properties;
   }

   public int getTimeSpan() {
      return this.properties.getMybatisTimeSpan();
   }

   public String getDesc() {
      return this.getClass().getName();
   }

   public String getName() {
      return "kmc.monitor.collect.mybatis";
   }

   public boolean getEnabled() {
      return this.properties.isMybatisEnabled();
   }

   protected CollectInfo getData() {
      try {
         SqlMybatisInfo info = new SqlMybatisInfo();
         Collector collector = Collector.getCollector();
         if (Objects.nonNull(collector)) {
            Collector.Hook hook = collector.hook("kmc.monitor.mybatis.sql.hook");
            info.hookCurrent = hook.getCurrent();
            info.hookError = hook.getLastErrorPerSecond();
            info.hookSuccess = hook.getLastSuccessPerSecond();
            info.hookList = hook.getMaxTimeSpanList().toText();
            info.hookListPerMinute = hook.getMaxTimeSpanListPerMinute().toText();
            return info;
         }
      } catch (Exception e) {
         if (LogUtils.isErrorEnabled()) {
            LogUtils.error(e);
         }
      }

      return null;
   }

   private static class SqlMybatisInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.mybatis.sql.hook.error",
         desc = "mybatis \u62e6\u622a\u4e0a\u4e00\u6b21\u6bcf\u79d2\u51fa\u9519\u6b21\u6570"
      )
      private Long hookError = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.mybatis.sql.hook.success",
         desc = "mybatis \u62e6\u622a\u4e0a\u4e00\u6b21\u6bcf\u79d2\u6210\u529f\u6b21\u6570"
      )
      private Long hookSuccess = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.mybatis.sql.hook.current",
         desc = "mybatis \u62e6\u622a\u5f53\u524d\u6267\u884c\u4efb\u52a1\u6570"
      )
      private Long hookCurrent = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.mybatis.sql.hook.list.detail",
         desc = "mybatis \u62e6\u622a\u5386\u53f2\u6700\u5927\u8017\u65f6\u4efb\u52a1\u5217\u8868"
      )
      private String hookList = "";
      @FieldReport(
         name = "kmc.monitor.collect.mybatis.sql.hook.list.minute.detail",
         desc = "mybatis \u62e6\u622a\u5386\u53f2\u6700\u5927\u8017\u65f6\u4efb\u52a1\u5217\u8868(\u6bcf\u5206\u949f)"
      )
      private String hookListPerMinute = "";

      private SqlMybatisInfo() {
      }
   }
}
