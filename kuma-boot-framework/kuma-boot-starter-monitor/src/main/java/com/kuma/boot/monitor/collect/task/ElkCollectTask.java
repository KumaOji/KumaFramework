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
         desc = "ELK消息队列大小"
      )
      private Integer ringBufferSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.elk.consecutive.dropped.count",
         desc = "ELK消息连续丢弃数量"
      )
      private Long consecutiveDropped = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.elk.dropped.warn.frequency",
         desc = "ELK下降警告频率"
      )
      private Integer droppedWarnFrequency = 0;
      @FieldReport(
         name = "kmc.monitor.collect.elk.keep.alive.duration",
         desc = "ELK保持活动持续时间"
      )
      private Long keepAliveDuration = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.elk.producer.type",
         desc = "ELK生产者类型"
      )
      private String producerType = "";
      @FieldReport(
         name = "kmc.monitor.collect.elk.reconnection.delay",
         desc = "ELK在与目标的连接失败后，在尝试重新连接到该目标之前等待的时间段"
      )
      private Long reconnectionDelay = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.elk.secondary.connection.ttl",
         desc = "ELK辅助连接 TTL大小"
      )
      private Long secondaryConnectionTtl = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.elk.write.timeout",
         desc = "ELK在超时之前等待写入完成的时间段.并尝试重新连接到该目的地。"
      )
      private Long writeTimeout = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.elk.write.buffer.size",
         desc = "ELK消写入缓冲区中可用的字节数"
      )
      private Integer writeBufferSize = 0;

      private ElkInfo() {
      }
   }
}
