package com.kuma.boot.monitor.collect.task;

import com.kuma.boot.core.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.utils.reflect.ReflectionUtils;
import com.kuma.boot.core.support.Collector;
import com.kuma.boot.monitor.annotation.FieldReport;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.collect.AbstractCollectTask;
import com.kuma.boot.monitor.collect.CollectInfo;
import java.util.Objects;

public class JedisCollectTask extends AbstractCollectTask {
   private static final String TASK_NAME = "kmc.monitor.collect.jedis";
   private final CollectTaskProperties properties;
   private final Collector collector;

   public JedisCollectTask(Collector collector, CollectTaskProperties properties) {
      this.collector = collector;
      this.properties = properties;
   }

   public int getTimeSpan() {
      return this.properties.getJedisTimeSpan();
   }

   public String getDesc() {
      return this.getClass().getName();
   }

   public String getName() {
      return "kmc.monitor.collect.jedis";
   }

   public boolean getEnabled() {
      return this.properties.isJedisEnabled();
   }

   protected CollectInfo getData() {
      try {
         Object item = ContextUtils.getBean(ReflectionUtils.classForName("com.yh.csx.bsf.redis.impl.RedisClusterMonitor"), false);
         if (Objects.nonNull(item)) {
            ReflectionUtils.callMethod(item, "collect", (Object[])null);
            JedisInfo info = new JedisInfo();
            String name = "jedis.cluster";
            info.detail = (String)this.collector.value(name + ".pool.detail").get();
            info.wait = (Integer)this.collector.value(name + ".pool.wait").get();
            info.active = (Integer)this.collector.value(name + ".pool.active").get();
            info.idle = (Integer)this.collector.value(name + ".pool.idle").get();
            info.lockInfo = (String)this.collector.value(name + ".lock.error.detail").get();
            Collector.Hook hook = this.collector.hook(name + ".hook");
            if (hook != null) {
               info.hookCurrent = hook.getCurrent();
               info.hookError = hook.getLastErrorPerSecond();
               info.hookSuccess = hook.getLastSuccessPerSecond();
               info.hookList = hook.getMaxTimeSpanList().toText();
               info.hookListPerMinute = hook.getMaxTimeSpanListPerMinute().toText();
            }

            return info;
         }
      } catch (Exception e) {
         if (LogUtils.isErrorEnabled()) {
            LogUtils.error(e);
         }
      }

      return null;
   }

   private static class JedisInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.jedis.cluster.pool.wait",
         desc = "jedis集群排队等待的请求数"
      )
      private Integer wait = 0;
      @FieldReport(
         name = "kmc.monitor.collect.jedis.cluster.pool.active",
         desc = "jedis集群活动使用的请求数"
      )
      private Integer active = 0;
      @FieldReport(
         name = "kmc.monitor.collect.jedis.cluster.pool.idle",
         desc = "jedis集群空闲的请求数"
      )
      private Integer idle = 0;
      @FieldReport(
         name = "kmc.monitor.collect.jedis.cluster.pool.detail",
         desc = "jedis集群连接池详情"
      )
      private String detail = " = 0L";
      @FieldReport(
         name = "kmc.monitor.collect.jedis.cluster.hook.error",
         desc = "jedis集群拦截上一次每秒出错次数"
      )
      private Long hookError = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.jedis.cluster.hook.success",
         desc = "jedis集群拦截上一次每秒成功次数"
      )
      private Long hookSuccess = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.jedis.cluster.hook.current",
         desc = "jedis集群拦截当前执行任务数"
      )
      private Long hookCurrent = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.jedis.cluster.hook.list.detail",
         desc = "jedis集群拦截历史最大耗时任务列表"
      )
      private String hookList = "";
      @FieldReport(
         name = "kmc.monitor.collect.jedis.cluster.hook.list.minute.detail",
         desc = "jedis集群拦截历史最大耗时任务列表(每分钟)"
      )
      private String hookListPerMinute = "";
      @FieldReport(
         name = "kmc.monitor.collect.jedis.cluster.lock.error.detail",
         desc = "jedis集群分布式锁异常信息"
      )
      private String lockInfo = "";

      private JedisInfo() {
      }
   }
}
