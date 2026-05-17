package com.kuma.boot.monitor.collect.task;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.utils.reflect.ReflectionUtils;
import com.kuma.boot.core.utils.BootContextUtils;
import com.kuma.boot.monitor.annotation.FieldReport;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.collect.AbstractCollectTask;
import com.kuma.boot.monitor.collect.CollectInfo;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.boot.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.context.ConfigurableWebServerApplicationContext;

public class WebServerCollectTask extends AbstractCollectTask {
   private static final String TASK_NAME = "kmc.monitor.collect.webserver";
   private final CollectTaskProperties collectTaskProperties;

   public WebServerCollectTask(CollectTaskProperties collectTaskProperties) {
      this.collectTaskProperties = collectTaskProperties;
   }

   public int getTimeSpan() {
      return this.collectTaskProperties.getWebServerTimeSpan();
   }

   public String getDesc() {
      return this.getClass().getName();
   }

   public String getName() {
      return "kmc.monitor.collect.webserver";
   }

   public boolean getEnabled() {
      return this.collectTaskProperties.getWebServerEnabled();
   }

   protected CollectInfo getData() {
      try {
         ConfigurableWebServerApplicationContext context = BootContextUtils.getConfigurableWebServerApplicationContext();
         if (context != null) {
            WebServer webServer = context.getWebServer();
            if (webServer instanceof TomcatWebServer) {
               Object getTomcat = ReflectionUtils.callMethod(webServer, "getTomcat", (Object[])null);
               Object getConnector = ReflectionUtils.callMethod(getTomcat, "getConnector", (Object[])null);
               Object getProtocolHandler = ReflectionUtils.callMethod(getConnector, "getProtocolHandler", (Object[])null);
               Object executor = ReflectionUtils.callMethod(getProtocolHandler, "getExecutor", (Object[])null);
               Class<?> poolCls = ReflectionUtils.tryClassForName("org.apache.tomcat.util.threads.ThreadPoolExecutor");
               if (executor != null && poolCls.isAssignableFrom(executor.getClass()) && executor instanceof ThreadPoolExecutor) {
                  ThreadPoolExecutor pool = (ThreadPoolExecutor)executor;
                  TomcatInfo tomcatInfo = new TomcatInfo();
                  tomcatInfo.activeCount = pool.getActiveCount();
                  tomcatInfo.corePoolSize = pool.getCorePoolSize();
                  tomcatInfo.poolSizeCount = pool.getPoolSize();
                  tomcatInfo.poolSizeMax = pool.getMaximumPoolSize();
                  tomcatInfo.poolSizeLargest = pool.getLargestPoolSize();
                  tomcatInfo.queueSize = pool.getQueue().size();
                  tomcatInfo.taskCount = pool.getTaskCount();
                  tomcatInfo.taskCompleted = pool.getCompletedTaskCount();
                  return tomcatInfo;
               }
            }
         }
      } catch (Exception e) {
         if (LogUtils.isErrorEnabled()) {
            LogUtils.error(e);
         }
      }

      return null;
   }

   private static class UndertowInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.webserver.buffer.size",
         desc = "undertow buffer\u5927\u5c0f"
      )
      private Integer bufferSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.io.threads",
         desc = "undertow io\u7ebf\u7a0b\u6570"
      )
      private Integer ioThreads = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.worker.threads",
         desc = "undertow \u5de5\u4f5c\u7ebf\u7a0b\u6570"
      )
      private Integer workerThreads = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.listeners.size",
         desc = "undertow listeners\u6570\u91cf"
      )
      private Integer listeners = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.listenerinfo.size",
         desc = "undertow listenerInfo\u6570\u91cf"
      )
      private Integer listenerInfo = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.internal.worker",
         desc = "undertow \u662f\u5426\u5185\u90e8work"
      )
      private Boolean internalWorker = false;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.byte.buffer.pool",
         desc = "undertow byteBufferPool\u5927\u5c0f"
      )
      private Integer byteBufferPool = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.channels.size",
         desc = "undertow \u7ba1\u9053\u6570\u91cf"
      )
      private Integer channels = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.worker.options.size",
         desc = "undertow workerOptions\u6570\u91cf"
      )
      private Integer workerOptions = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.socket.options.size",
         desc = "undertow socketOptions\u6570\u91cf"
      )
      private Integer socketOptions = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.server.options.size",
         desc = "undertow serverOptions\u6570\u91cf"
      )
      private Integer serverOptions = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.xino.name",
         desc = "undertow xion \u540d\u79f0"
      )
      private String xinoName = "";
      @FieldReport(
         name = "kmc.monitor.collect.webserver.work.name",
         desc = "undertow work \u540d\u79f0"
      )
      private String workName = "";
      @FieldReport(
         name = "kmc.monitor.collect.webserver.worker.core.pool.size",
         desc = "undertow \u6838\u5fc3\u5de5\u4f5c\u7ebf\u7a0b\u6c60\u5927\u5c0f"
      )
      private Integer workerCorePoolSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.worker.max.pool.size",
         desc = "undertow \u6700\u5927\u5de5\u4f5c\u7ebf\u7a0b\u6c60\u5927\u5c0f"
      )
      private Integer workerMaxPoolSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.worker.io.thread.count",
         desc = "undertow I/O \u7ebf\u7a0b\u6570"
      )
      private Integer workerIoThreadCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.worker.busy.thread.count",
         desc = "undertow \u5de5\u4f5c\u6c60\u4e2d\u7e41\u5fd9\u7ebf\u7a0b\u6570"
      )
      private Integer workerBusyThreadCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.worker.provider.name",
         desc = "undertow work\u63d0\u4f9b\u8005\u540d\u79f0"
      )
      private String workerProviderName = "";
      @FieldReport(
         name = "kmc.monitor.collect.webserver.worker.werver.mxbeans.size",
         desc = "undertow server mxbean\u6570\u91cf"
      )
      private Integer workerServerMXBeans = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.worker.pool.size",
         desc = "undertow \u83b7\u53d6\u5de5\u4f5c\u6c60\u4e2d\u7ebf\u7a0b\u6570"
      )
      private Integer workerPoolSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.worker.queue.size",
         desc = "undertow \u5de5\u4f5c\u961f\u5217\u4e2d\u4efb\u52a1\u6570\u91cf"
      )
      private Integer workerQueueSize = 0;

      private UndertowInfo() {
      }
   }

   private static class TomcatInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.webserver.active.count",
         desc = "tomcat \u7ebf\u7a0b\u6c60\u6d3b\u52a8\u7ebf\u7a0b\u6570"
      )
      private Integer activeCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.core.pool.size",
         desc = "tomcat \u7ebf\u7a0b\u6c60\u6838\u5fc3\u7ebf\u7a0b\u6570"
      )
      private Integer corePoolSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.pool.size.largest",
         desc = "tomcat \u7ebf\u7a0b\u6c60\u5386\u53f2\u6700\u5927\u7ebf\u7a0b\u6570"
      )
      private Integer poolSizeLargest = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.pool.size.max",
         desc = "tomcat \u7ebf\u7a0b\u6c60\u6700\u5927\u7ebf\u7a0b\u6570"
      )
      private Integer poolSizeMax = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.pool.size.count",
         desc = "tomcat \u7ebf\u7a0b\u6c60\u5f53\u524d\u7ebf\u7a0b\u6570"
      )
      private Integer poolSizeCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.queue.size",
         desc = "tomcat \u7ebf\u7a0b\u6c60\u5f53\u524d\u6392\u961f\u7b49\u5f85\u4efb\u52a1\u6570"
      )
      private Integer queueSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.task.count",
         desc = "tomcat \u7ebf\u7a0b\u6c60\u5386\u53f2\u4efb\u52a1\u6570"
      )
      private Long taskCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.task.completed",
         desc = "tomcat \u7ebf\u7a0b\u6c60\u5df2\u5b8c\u6210\u4efb\u52a1\u6570"
      )
      private Long taskCompleted = 0L;

      private TomcatInfo() {
      }
   }
}
