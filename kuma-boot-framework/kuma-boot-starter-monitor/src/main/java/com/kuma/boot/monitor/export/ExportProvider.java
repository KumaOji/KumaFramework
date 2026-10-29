package com.kuma.boot.monitor.export;

import com.kuma.boot.core.utils.context.ContextUtils;
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

      this.monitorThreadPool.monitorSubmit("系统任务: ExportProvider 采集上传任务", (Runnable)(() -> {
         while(!this.monitorThreadPool.monitorIsShutdown() && !this.isClose) {
            try {
               this.run();
            } catch (Exception e) {
               LogUtils.error("kuma-boot-starter-monitor", new Object[]{"run 循环上传报表出错", e});
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
            LogUtils.error("kuma-boot-starter-monitor", new Object[]{e.getClass().getName() + "启动出错", ex});
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
            LogUtils.error(ex, "kuma-boot-starter-monitor", new Object[]{e.getClass().getName() + "关闭出错"});
         }
      }

   }
}
