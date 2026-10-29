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
         desc = "mybatis 拦截上一次每秒出错次数"
      )
      private Long hookError = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.mybatis.sql.hook.success",
         desc = "mybatis 拦截上一次每秒成功次数"
      )
      private Long hookSuccess = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.mybatis.sql.hook.current",
         desc = "mybatis 拦截当前执行任务数"
      )
      private Long hookCurrent = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.mybatis.sql.hook.list.detail",
         desc = "mybatis 拦截历史最大耗时任务列表"
      )
      private String hookList = "";
      @FieldReport(
         name = "kmc.monitor.collect.mybatis.sql.hook.list.minute.detail",
         desc = "mybatis 拦截历史最大耗时任务列表(每分钟)"
      )
      private String hookListPerMinute = "";

      private SqlMybatisInfo() {
      }
   }
}
