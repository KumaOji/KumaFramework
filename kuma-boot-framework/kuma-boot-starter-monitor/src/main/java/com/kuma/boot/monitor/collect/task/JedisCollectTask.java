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
         desc = "jedis\u96c6\u7fa4\u6392\u961f\u7b49\u5f85\u7684\u8bf7\u6c42\u6570"
      )
      private Integer wait = 0;
      @FieldReport(
         name = "kmc.monitor.collect.jedis.cluster.pool.active",
         desc = "jedis\u96c6\u7fa4\u6d3b\u52a8\u4f7f\u7528\u7684\u8bf7\u6c42\u6570"
      )
      private Integer active = 0;
      @FieldReport(
         name = "kmc.monitor.collect.jedis.cluster.pool.idle",
         desc = "jedis\u96c6\u7fa4\u7a7a\u95f2\u7684\u8bf7\u6c42\u6570"
      )
      private Integer idle = 0;
      @FieldReport(
         name = "kmc.monitor.collect.jedis.cluster.pool.detail",
         desc = "jedis\u96c6\u7fa4\u8fde\u63a5\u6c60\u8be6\u60c5"
      )
      private String detail = " = 0L";
      @FieldReport(
         name = "kmc.monitor.collect.jedis.cluster.hook.error",
         desc = "jedis\u96c6\u7fa4\u62e6\u622a\u4e0a\u4e00\u6b21\u6bcf\u79d2\u51fa\u9519\u6b21\u6570"
      )
      private Long hookError = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.jedis.cluster.hook.success",
         desc = "jedis\u96c6\u7fa4\u62e6\u622a\u4e0a\u4e00\u6b21\u6bcf\u79d2\u6210\u529f\u6b21\u6570"
      )
      private Long hookSuccess = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.jedis.cluster.hook.current",
         desc = "jedis\u96c6\u7fa4\u62e6\u622a\u5f53\u524d\u6267\u884c\u4efb\u52a1\u6570"
      )
      private Long hookCurrent = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.jedis.cluster.hook.list.detail",
         desc = "jedis\u96c6\u7fa4\u62e6\u622a\u5386\u53f2\u6700\u5927\u8017\u65f6\u4efb\u52a1\u5217\u8868"
      )
      private String hookList = "";
      @FieldReport(
         name = "kmc.monitor.collect.jedis.cluster.hook.list.minute.detail",
         desc = "jedis\u96c6\u7fa4\u62e6\u622a\u5386\u53f2\u6700\u5927\u8017\u65f6\u4efb\u52a1\u5217\u8868(\u6bcf\u5206\u949f)"
      )
      private String hookListPerMinute = "";
      @FieldReport(
         name = "kmc.monitor.collect.jedis.cluster.lock.error.detail",
         desc = "jedis\u96c6\u7fa4\u5206\u5e03\u5f0f\u9501\u5f02\u5e38\u4fe1\u606f"
      )
      private String lockInfo = "";

      private JedisInfo() {
      }
   }
}
