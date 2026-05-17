package com.kuma.boot.monitor.collect.task;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ElasticSearchCollectTask extends AbstractCollectTask {
   private static final String TASK_NAME = "kmc.monitor.collect.elasticsearch";
   private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
   private final CollectTaskProperties properties;
   private HashMap<Long, Long> lastThreadUserTime = new HashMap();
   private final boolean classExist;

   public ElasticSearchCollectTask(CollectTaskProperties properties) {
      this.properties = properties;
      this.classExist = ClassUtils.isExist("com.kuma.boot.monitor.elasticsearch.impl.ElasticSearchProvider");
   }

   public int getTimeSpan() {
      return this.properties.getElasticSearchTimeSpan();
   }

   public boolean getEnabled() {
      return this.properties.isElasticSearchEnabled() && this.classExist;
   }

   public String getDesc() {
      return this.getClass().getName();
   }

   public String getName() {
      return "kmc.monitor.collect.elasticsearch";
   }

   protected CollectInfo getData() {
      ElasticSearchData data = new ElasticSearchData();
      if (ContextUtils.getBean(ReflectionUtils.tryClassForName("com.kuma.boot.monitor.elasticsearch.impl.ElasticSearchProvider"), false) != null) {
         Collector.Hook hook = (Collector.Hook)ReflectionUtils.callMethod(ReflectionUtils.tryClassForName("com.kuma.boot.monitor.elasticsearch.impl.ElasticSearchMonitor"), "hook", (Object[])null);
         if (hook != null) {
            data.hookCurrent = hook.getCurrent();
            data.hookError = hook.getLastErrorPerSecond();
            data.hookSuccess = hook.getLastSuccessPerSecond();
            data.hookList = hook.getMaxTimeSpanList().toText();
            data.hookListPerMinute = hook.getMaxTimeSpanListPerMinute().toText();
         }

         Object transportClient = ContextUtils.getBean(ReflectionUtils.tryClassForName("org.elasticsearch.client.transport.TransportClient"), false);
         if (transportClient != null) {
            data.threadsTotal = 0;
            data.activateTotal = 0;
            data.queueSize = 0;
            Iterator<Object> stats = (Iterator)ReflectionUtils.tryGetValue(transportClient, "threadPool.stats.iterator");
            if (stats != null) {
               stats.forEachRemaining((stat) -> {
                  data.threadsTotal = data.threadsTotal + (Integer)ReflectionUtils.tryGetValue(stat, "threads", 0);
                  data.activateTotal = data.activateTotal + (Integer)ReflectionUtils.tryGetValue(stat, "active", 0);
                  data.queueSize = data.queueSize + (Integer)ReflectionUtils.tryGetValue(stat, "queue", 0);
               });
            }

            ScheduledThreadPoolExecutor scheduler = (ScheduledThreadPoolExecutor)ReflectionUtils.tryGetValue(transportClient, "threadPool.scheduler");
            if (scheduler != null) {
               data.threadsTotal = data.threadsTotal + scheduler.getPoolSize();
               data.activateTotal = data.activateTotal + scheduler.getActiveCount();
               data.queueSize = data.queueSize + scheduler.getQueue().size();
            }

            Object nodesService = ReflectionUtils.tryGetValue(transportClient, "nodesService");
            if (nodesService != null) {
               data.nodesCount = (Integer)ReflectionUtils.tryGetValue(nodesService, "listedNodes.size");
               data.connectedCount = (Integer)ReflectionUtils.tryGetValue(nodesService, "nodes.size");
               data.connectionCount = 0;
               Collection<Object> channels = (Collection)ReflectionUtils.tryGetValue(nodesService, "transportService.connectionManager.connectedNodes.values");
               if (channels != null) {
                  for(Object obj : channels) {
                     data.connectionCount = data.connectionCount + (Integer)ReflectionUtils.tryGetValue(obj, "channels.size", 0);
                  }
               }
            }
         }
      }

      return data;
   }

   private static class ElasticSearchData implements CollectInfo {
      @FieldReport(
         name = "elasticSearch.hook.error",
         desc = "ElasticSearch\u670d\u52a1\u62e6\u622a\u4e0a\u4e00\u6b21\u6bcf\u79d2\u51fa\u9519\u6b21\u6570"
      )
      private Long hookError;
      @FieldReport(
         name = "elasticSearch.hook.success",
         desc = "ElasticSearch\u670d\u52a1\u62e6\u622a\u4e0a\u4e00\u6b21\u6bcf\u79d2\u6210\u529f\u6b21\u6570"
      )
      private Long hookSuccess;
      @FieldReport(
         name = "elasticSearch.hook.current",
         desc = "ElasticSearch\u670d\u52a1\u62e6\u622a\u5f53\u524d\u6267\u884c\u4efb\u52a1\u6570"
      )
      private Long hookCurrent;
      @FieldReport(
         name = "elasticSearch.hook.list.detail",
         desc = "ElasticSearch\u670d\u52a1\u62e6\u622a\u5386\u53f2\u6700\u5927\u8017\u65f6\u4efb\u52a1\u5217\u8868"
      )
      private String hookList;
      @FieldReport(
         name = "elasticSearch.hook.list.minute.detail",
         desc = "ElasticSearch\u670d\u52a1\u62e6\u622a\u5386\u53f2\u6700\u5927\u8017\u65f6\u4efb\u52a1\u5217\u8868(\u6bcf\u5206\u949f)"
      )
      private String hookListPerMinute;
      @FieldReport(
         name = "elasticSearch.node.count",
         desc = "ElasticSearch\u96c6\u7fa4\u53d1\u73b0\u8282\u70b9\u6570"
      )
      private Integer nodesCount;
      @FieldReport(
         name = "elasticSearch.node.connected",
         desc = "ElasticSearch\u96c6\u7fa4\u5df2\u8fde\u63a5\u8282\u70b9\u6570"
      )
      private Integer connectedCount;
      @FieldReport(
         name = "elasticSearch.node.connections",
         desc = "ElasticSearch\u96c6\u7fa4\u8fde\u63a5\u6570"
      )
      private Integer connectionCount;
      @FieldReport(
         name = "elasticSearch.pool.threads.count",
         desc = "ElasticSearch\u96c6\u7fa4\u7ebf\u7a0b\u6c60\u7ebf\u7a0b\u6570"
      )
      private Integer threadsTotal;
      @FieldReport(
         name = "elasticSearch.pool.threads.active",
         desc = "ElasticSearch\u96c6\u7fa4\u6c60\u7ebf\u7a0b\u6d3b\u52a8\u7ebf\u7a0b\u6570"
      )
      private Integer activateTotal;
      @FieldReport(
         name = "elasticSearch.pool.queue.size",
         desc = "ElasticSearch\u96c6\u7fa4\u6c60\u7ebf\u7a0b\u961f\u5217\u5927\u5c0f"
      )
      private Integer queueSize;

      private ElasticSearchData() {
      }
   }
}
