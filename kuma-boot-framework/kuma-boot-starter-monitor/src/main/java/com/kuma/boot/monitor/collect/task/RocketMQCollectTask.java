package com.kuma.boot.monitor.collect.task;

import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.core.utils.context.ContextUtils;
import com.kuma.boot.core.utils.reflect.ClassUtils;
import com.kuma.boot.core.utils.reflect.ReflectionUtils;
import com.kuma.boot.core.support.Collector;
import com.kuma.boot.monitor.annotation.FieldReport;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.collect.AbstractCollectTask;
import com.kuma.boot.monitor.collect.CollectInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;

public class RocketMQCollectTask extends AbstractCollectTask {
   private static final String TASK_NAME = "kmc.monitor.collect.rocket";
   private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
   private final CollectTaskProperties properties;
   private HashMap<Long, Long> lastThreadUserTime = new HashMap();
   private final boolean classExist;

   public RocketMQCollectTask(CollectTaskProperties properties) {
      this.properties = properties;
      this.classExist = ClassUtils.isExist("com.kuma.boot.monitor.rocketmq.RocketMQConsumerProvider");
   }

   public int getTimeSpan() {
      return this.properties.getRocketMQTimeSpan();
   }

   public boolean getEnabled() {
      return this.properties.isRocketMQEnabled() && this.classExist;
   }

   public String getDesc() {
      return this.getClass().getName();
   }

   public String getName() {
      return "kmc.monitor.collect.rocket";
   }

   protected CollectInfo getData() {
      RocketMQInfo data = new RocketMQInfo();
      if (ContextUtils.getBean(ReflectionUtils.tryClassForName("com.kuma.boot.monitor.rocketmq.RocketMQConsumerProvider"), false) != null) {
         Collector.Hook hook = this.getCollectorHook("com.kuma.boot.monitor.rocketmq.RocketMQMonitor");
         data.consumerHookCurrent = hook.getCurrent();
         data.consumerHookError = hook.getLastErrorPerSecond();
         data.consumerHookSuccess = hook.getLastSuccessPerSecond();
         data.consumerHookList = hook.getMaxTimeSpanList().toText();
         data.consumerHookListPerMinute = hook.getMaxTimeSpanListPerMinute().toText();
      }

      if (ContextUtils.getBean(ReflectionUtils.tryClassForName("com.kuma.boot.monitor.rocketmq.RocketMQProducerProvider"), false) != null) {
         Collector.Hook hook = this.getCollectorHook("com.kuma.boot.monitor.rocketmq.RocketMQMonitor");
         data.producerHookCurrent = hook.getCurrent();
         data.producerHookError = hook.getLastErrorPerSecond();
         data.producerHookSuccess = hook.getLastSuccessPerSecond();
         data.producerHookList = hook.getMaxTimeSpanList().toText();
         data.producerHookListPerMinute = hook.getMaxTimeSpanListPerMinute().toText();
      }

      return data;
   }

   private Collector.Hook getCollectorHook(String className) {
      Class<?> monitor = ReflectionUtils.classForName(className);

      try {
         return (Collector.Hook)ReflectionUtils.findMethod(monitor, "hook").invoke((Object)null);
      } catch (Exception e) {
         throw new BaseException(e);
      }
   }

   private static class RocketMQInfo implements CollectInfo {
      @FieldReport(
         name = "rocketmq.consumer.hook.error",
         desc = "Consumer\u62e6\u622a\u4e0a\u4e00\u6b21\u6bcf\u79d2\u51fa\u9519\u6b21\u6570"
      )
      private Long consumerHookError;
      @FieldReport(
         name = "rocketmq.consumer.hook.success",
         desc = "Consumer\u62e6\u622a\u4e0a\u4e00\u6b21\u6bcf\u79d2\u6210\u529f\u6b21\u6570"
      )
      private Long consumerHookSuccess;
      @FieldReport(
         name = "rocketmq.consumer.hook.current",
         desc = "Consumer\u62e6\u622a\u5f53\u524d\u6267\u884c\u4efb\u52a1\u6570"
      )
      private Long consumerHookCurrent;
      @FieldReport(
         name = "rocketmq.consumer.hook.list.detail",
         desc = "Consumer\u62e6\u622a\u5386\u53f2\u6700\u5927\u8017\u65f6\u4efb\u52a1\u5217\u8868"
      )
      private String consumerHookList;
      @FieldReport(
         name = "rocketmq.consumer.hook.list.minute.detail",
         desc = "Consumer\u62e6\u622a\u5386\u53f2\u6700\u5927\u8017\u65f6\u4efb\u52a1\u5217\u8868(\u6bcf\u5206\u949f)"
      )
      private String consumerHookListPerMinute;
      @FieldReport(
         name = "rocketmq.producer.hook.error",
         desc = "Producer\u62e6\u622a\u4e0a\u4e00\u6b21\u6bcf\u79d2\u51fa\u9519\u6b21\u6570"
      )
      private Long producerHookError;
      @FieldReport(
         name = "rocketmq.producer.hook.success",
         desc = "Producer\u62e6\u622a\u4e0a\u4e00\u6b21\u6bcf\u79d2\u6210\u529f\u6b21\u6570"
      )
      private Long producerHookSuccess;
      @FieldReport(
         name = "rocketmq.producer.hook.current",
         desc = "Producer\u62e6\u622a\u5f53\u524d\u6267\u884c\u4efb\u52a1\u6570"
      )
      private Long producerHookCurrent;
      @FieldReport(
         name = "rocketmq.producer.hook.list.detail",
         desc = "Producer\u62e6\u622a\u5386\u53f2\u6700\u5927\u8017\u65f6\u4efb\u52a1\u5217\u8868"
      )
      private String producerHookList;
      @FieldReport(
         name = "rocketmq.producer.hook.list.minute.detail",
         desc = "Producer\u62e6\u622a\u5386\u53f2\u6700\u5927\u8017\u65f6\u4efb\u52a1\u5217\u8868(\u6bcf\u5206\u949f)"
      )
      private String producerHookListPerMinute;

      private RocketMQInfo() {
      }
   }
}
