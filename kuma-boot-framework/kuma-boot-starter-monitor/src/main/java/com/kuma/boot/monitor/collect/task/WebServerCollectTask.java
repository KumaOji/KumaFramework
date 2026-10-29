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
         desc = "undertow buffer大小"
      )
      private Integer bufferSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.io.threads",
         desc = "undertow io线程数"
      )
      private Integer ioThreads = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.worker.threads",
         desc = "undertow 工作线程数"
      )
      private Integer workerThreads = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.listeners.size",
         desc = "undertow listeners数量"
      )
      private Integer listeners = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.listenerinfo.size",
         desc = "undertow listenerInfo数量"
      )
      private Integer listenerInfo = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.internal.worker",
         desc = "undertow 是否内部work"
      )
      private Boolean internalWorker = false;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.byte.buffer.pool",
         desc = "undertow byteBufferPool大小"
      )
      private Integer byteBufferPool = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.channels.size",
         desc = "undertow 管道数量"
      )
      private Integer channels = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.worker.options.size",
         desc = "undertow workerOptions数量"
      )
      private Integer workerOptions = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.socket.options.size",
         desc = "undertow socketOptions数量"
      )
      private Integer socketOptions = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.server.options.size",
         desc = "undertow serverOptions数量"
      )
      private Integer serverOptions = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.xino.name",
         desc = "undertow xion 名称"
      )
      private String xinoName = "";
      @FieldReport(
         name = "kmc.monitor.collect.webserver.work.name",
         desc = "undertow work 名称"
      )
      private String workName = "";
      @FieldReport(
         name = "kmc.monitor.collect.webserver.worker.core.pool.size",
         desc = "undertow 核心工作线程池大小"
      )
      private Integer workerCorePoolSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.worker.max.pool.size",
         desc = "undertow 最大工作线程池大小"
      )
      private Integer workerMaxPoolSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.worker.io.thread.count",
         desc = "undertow I/O 线程数"
      )
      private Integer workerIoThreadCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.worker.busy.thread.count",
         desc = "undertow 工作池中繁忙线程数"
      )
      private Integer workerBusyThreadCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.worker.provider.name",
         desc = "undertow work提供者名称"
      )
      private String workerProviderName = "";
      @FieldReport(
         name = "kmc.monitor.collect.webserver.worker.werver.mxbeans.size",
         desc = "undertow server mxbean数量"
      )
      private Integer workerServerMXBeans = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.worker.pool.size",
         desc = "undertow 获取工作池中线程数"
      )
      private Integer workerPoolSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.worker.queue.size",
         desc = "undertow 工作队列中任务数量"
      )
      private Integer workerQueueSize = 0;

      private UndertowInfo() {
      }
   }

   private static class TomcatInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.webserver.active.count",
         desc = "tomcat 线程池活动线程数"
      )
      private Integer activeCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.core.pool.size",
         desc = "tomcat 线程池核心线程数"
      )
      private Integer corePoolSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.pool.size.largest",
         desc = "tomcat 线程池历史最大线程数"
      )
      private Integer poolSizeLargest = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.pool.size.max",
         desc = "tomcat 线程池最大线程数"
      )
      private Integer poolSizeMax = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.pool.size.count",
         desc = "tomcat 线程池当前线程数"
      )
      private Integer poolSizeCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.queue.size",
         desc = "tomcat 线程池当前排队等待任务数"
      )
      private Integer queueSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.task.count",
         desc = "tomcat 线程池历史任务数"
      )
      private Long taskCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.webserver.task.completed",
         desc = "tomcat 线程池已完成任务数"
      )
      private Long taskCompleted = 0L;

      private TomcatInfo() {
      }
   }
}
