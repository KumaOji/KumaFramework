package com.kuma.boot.monitor.export;

import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.Monitor;
import com.kuma.boot.monitor.autoconfigure.properties.ExportProperties;
import com.kuma.boot.monitor.collect.HealthCheckProvider;
import com.kuma.boot.monitor.model.Report;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.logstash.logback.appender.LogstashTcpSocketAppender;

public class ExportProvider {
   private boolean isClose = true;
   private final Monitor monitorThreadPool;
   private final ExportProperties exportProperties;
   private final HealthCheckProvider healthCheckProvider;
   protected List<AbstractExport> exports = new ArrayList();

   public ExportProvider(Monitor monitorThreadPool, ExportProperties exportProperties, HealthCheckProvider healthCheckProvider) {
      this.monitorThreadPool = monitorThreadPool;
      this.exportProperties = exportProperties;
      this.healthCheckProvider = healthCheckProvider;
   }

   public void registerCollectTask(AbstractExport export) {
      this.exports.add(export);
   }

   public void start() {
      this.isClose = false;
      if (this.exportProperties.getElkEnabled()) {
         LogstashTcpSocketAppender logstashTcpSocketAppender = (LogstashTcpSocketAppender)ContextUtils.getBean(LogstashTcpSocketAppender.class, false);
         if (Objects.nonNull(logstashTcpSocketAppender)) {
            this.registerCollectTask(new ElkExport(this.exportProperties, logstashTcpSocketAppender));
         }
      }

      this.monitorThreadPool.monitorSubmit("\u7cfb\u7edf\u4efb\u52a1: ExportProvider \u91c7\u96c6\u4e0a\u4f20\u4efb\u52a1", (Runnable)(() -> {
         while(!this.monitorThreadPool.monitorIsShutdown() && !this.isClose) {
            try {
               this.run();
            } catch (Exception e) {
               LogUtils.error("kuma-boot-starter-monitor", new Object[]{"run \u5faa\u73af\u4e0a\u4f20\u62a5\u8868\u51fa\u9519", e});
            }

            try {
               Thread.sleep((long)this.exportProperties.getExportTimeSpan() * 1000L);
            } catch (Exception e) {
               LogUtils.error(e);
            }
         }

      }));

      for(AbstractExport e : this.exports) {
         try {
            e.start();
         } catch (Exception ex) {
            LogUtils.error("kuma-boot-starter-monitor", new Object[]{e.getClass().getName() + "\u542f\u52a8\u51fa\u9519", ex});
         }
      }

   }

   public void run() {
      if (this.healthCheckProvider != null) {
         Report report = this.healthCheckProvider.getReport(false);

         for(AbstractExport e : this.exports) {
            e.run(report);
         }
      }

   }

   public void close() {
      this.isClose = true;

      for(AbstractExport e : this.exports) {
         try {
            e.close();
         } catch (Exception ex) {
            LogUtils.error(ex, "kuma-boot-starter-monitor", new Object[]{e.getClass().getName() + "\u5173\u95ed\u51fa\u9519"});
         }
      }

   }
}
