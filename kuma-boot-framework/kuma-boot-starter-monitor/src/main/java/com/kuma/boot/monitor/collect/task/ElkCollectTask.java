package com.kuma.boot.monitor.collect.task;

import ch.qos.logback.core.util.Duration;
import com.kuma.boot.core.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.utils.reflect.ClassUtils;
import com.kuma.boot.core.utils.reflect.ReflectionUtils;
import com.kuma.boot.monitor.annotation.FieldReport;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.collect.AbstractCollectTask;
import com.kuma.boot.monitor.collect.CollectInfo;
import java.util.Objects;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.appender.destination.PreferPrimaryDestinationConnectionStrategy;

public class ElkCollectTask extends AbstractCollectTask {
   private static final String TASK_NAME = "kmc.monitor.collect.elk";
   private final CollectTaskProperties properties;
   private final boolean classExist;

   public ElkCollectTask(CollectTaskProperties properties) {
      this.properties = properties;
      this.classExist = ClassUtils.isExist("net.logstash.logback.appender.LogstashTcpSocketAppender");
   }

   public int getTimeSpan() {
      return this.properties.getElkTimeSpan();
   }

   public boolean getEnabled() {
      return this.properties.isElkEnabled() && this.classExist;
   }

   public String getDesc() {
      return this.getClass().getName();
   }

   public String getName() {
      return "kmc.monitor.collect.elk";
   }

   protected CollectInfo getData() {
      try {
         ElkInfo info = new ElkInfo();
         LogstashTcpSocketAppender appender = (LogstashTcpSocketAppender)ContextUtils.getBean(LogstashTcpSocketAppender.class, true);
         if (Objects.nonNull(appender)) {
            info.ringBufferSize = appender.getRingBufferSize();
            info.consecutiveDropped = (Long)ReflectionUtils.tryGetValue(appender, "consecutiveDroppedCount.get");
            info.droppedWarnFrequency = appender.getDroppedWarnFrequency();

            try {
               info.keepAliveDuration = appender.getKeepAliveDuration().getMilliseconds();
               info.producerType = appender.getProducerType().name();
               info.reconnectionDelay = appender.getReconnectionDelay().getMilliseconds();
            } catch (Exception var4) {
            }

            Duration duration = appender.getConnectionStrategy() instanceof PreferPrimaryDestinationConnectionStrategy ? ((PreferPrimaryDestinationConnectionStrategy)appender.getConnectionStrategy()).getSecondaryConnectionTTL() : null;
            if (Objects.nonNull(duration)) {
               info.secondaryConnectionTtl = duration.getMilliseconds();
            }

            info.writeTimeout = appender.getWriteTimeout().getMilliseconds();
            info.writeBufferSize = appender.getWriteBufferSize();
            return info;
         }
      } catch (Exception e) {
         if (LogUtils.isErrorEnabled()) {
            LogUtils.error(e);
         }
      }

      return null;
   }

   private static class ElkInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.elk.ring.buffer.size",
         desc = "ELK\u6d88\u606f\u961f\u5217\u5927\u5c0f"
      )
      private Integer ringBufferSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.elk.consecutive.dropped.count",
         desc = "ELK\u6d88\u606f\u8fde\u7eed\u4e22\u5f03\u6570\u91cf"
      )
      private Long consecutiveDropped = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.elk.dropped.warn.frequency",
         desc = "ELK\u4e0b\u964d\u8b66\u544a\u9891\u7387"
      )
      private Integer droppedWarnFrequency = 0;
      @FieldReport(
         name = "kmc.monitor.collect.elk.keep.alive.duration",
         desc = "ELK\u4fdd\u6301\u6d3b\u52a8\u6301\u7eed\u65f6\u95f4"
      )
      private Long keepAliveDuration = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.elk.producer.type",
         desc = "ELK\u751f\u4ea7\u8005\u7c7b\u578b"
      )
      private String producerType = "";
      @FieldReport(
         name = "kmc.monitor.collect.elk.reconnection.delay",
         desc = "ELK\u5728\u4e0e\u76ee\u6807\u7684\u8fde\u63a5\u5931\u8d25\u540e\uff0c\u5728\u5c1d\u8bd5\u91cd\u65b0\u8fde\u63a5\u5230\u8be5\u76ee\u6807\u4e4b\u524d\u7b49\u5f85\u7684\u65f6\u95f4\u6bb5"
      )
      private Long reconnectionDelay = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.elk.secondary.connection.ttl",
         desc = "ELK\u8f85\u52a9\u8fde\u63a5 TTL\u5927\u5c0f"
      )
      private Long secondaryConnectionTtl = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.elk.write.timeout",
         desc = "ELK\u5728\u8d85\u65f6\u4e4b\u524d\u7b49\u5f85\u5199\u5165\u5b8c\u6210\u7684\u65f6\u95f4\u6bb5.\u5e76\u5c1d\u8bd5\u91cd\u65b0\u8fde\u63a5\u5230\u8be5\u76ee\u7684\u5730\u3002"
      )
      private Long writeTimeout = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.elk.write.buffer.size",
         desc = "ELK\u6d88\u5199\u5165\u7f13\u51b2\u533a\u4e2d\u53ef\u7528\u7684\u5b57\u8282\u6570"
      )
      private Integer writeBufferSize = 0;

      private ElkInfo() {
      }
   }
}
