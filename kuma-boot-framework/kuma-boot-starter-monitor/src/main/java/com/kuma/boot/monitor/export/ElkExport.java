package com.kuma.boot.monitor.export;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Context;
import cn.hutool.core.date.DateUtil;
import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.utils.common.PropertyUtils;
import com.kuma.boot.monitor.autoconfigure.properties.ExportProperties;
import com.kuma.boot.monitor.model.Report;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;
import net.logstash.logback.marker.MapEntriesAppendingMarker;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

public class ElkExport extends AbstractExport {
   private final ExportProperties exportProperties;
   private final LogstashTcpSocketAppender appender;

   public ElkExport(ExportProperties exportProperties, LogstashTcpSocketAppender appender) {
      this.exportProperties = exportProperties;
      this.appender = appender;
   }

   public void start() {
      super.start();
      ILoggerFactory log = LoggerFactory.getILoggerFactory();
      if (log instanceof Context) {
         this.appender.setContext((Context)log);
         LogstashEncoder encoder = new LogstashEncoder();
         String appName = "Report-" + (String)PropertyUtils.getPropertyCache(CommonConstants.SPRING_APP_NAME_KEY, "");
         encoder.setCustomFields("{\"appname\":\"" + appName + "\",\"appindex\":\"Report\"}");
         encoder.setEncoding("UTF-8");
         this.appender.setEncoder(encoder);
         this.appender.start();
      }

   }

   public void run(Report report) {
      if (this.appender != null && this.exportProperties.getElkEnabled()) {
         Map<String, Object> map = new LinkedHashMap();
         report.eachReport((field, reportItem) -> {
            if (reportItem != null && reportItem.getValue() instanceof Number) {
               map.put(field.replace(".", "_"), reportItem.getValue());
            }

            return reportItem;
         });
         Date var10002 = new Date();
         LoggingEvent event = this.createLoggerEvent(map, "kuma boot report:" + DateUtil.format(var10002, "yyyy-MM-dd HH:mm:ss"));
         this.appender.doAppend(event);
      }
   }

   private LoggingEvent createLoggerEvent(Map<String, Object> values, String message) {
      LoggingEvent loggingEvent = new LoggingEvent();
      loggingEvent.setTimeStamp(System.currentTimeMillis());
      loggingEvent.setLevel(Level.INFO);
      loggingEvent.setLoggerName("ReportLogger");
      loggingEvent.addMarker(new MapEntriesAppendingMarker(values));
      loggingEvent.setMessage(message);
      loggingEvent.setArgumentArray(new String[0]);
      loggingEvent.setThreadName(Thread.currentThread().getName());
      return loggingEvent;
   }

   public void close() {
      super.close();
      if (this.appender != null) {
         this.appender.stop();
      }

   }
}
