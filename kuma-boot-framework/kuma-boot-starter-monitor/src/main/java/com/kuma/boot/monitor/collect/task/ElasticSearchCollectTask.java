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
         desc = "ElasticSearch服务拦截上一次每秒出错次数"
      )
      private Long hookError;
      @FieldReport(
         name = "elasticSearch.hook.success",
         desc = "ElasticSearch服务拦截上一次每秒成功次数"
      )
      private Long hookSuccess;
      @FieldReport(
         name = "elasticSearch.hook.current",
         desc = "ElasticSearch服务拦截当前执行任务数"
      )
      private Long hookCurrent;
      @FieldReport(
         name = "elasticSearch.hook.list.detail",
         desc = "ElasticSearch服务拦截历史最大耗时任务列表"
      )
      private String hookList;
      @FieldReport(
         name = "elasticSearch.hook.list.minute.detail",
         desc = "ElasticSearch服务拦截历史最大耗时任务列表(每分钟)"
      )
      private String hookListPerMinute;
      @FieldReport(
         name = "elasticSearch.node.count",
         desc = "ElasticSearch集群发现节点数"
      )
      private Integer nodesCount;
      @FieldReport(
         name = "elasticSearch.node.connected",
         desc = "ElasticSearch集群已连接节点数"
      )
      private Integer connectedCount;
      @FieldReport(
         name = "elasticSearch.node.connections",
         desc = "ElasticSearch集群连接数"
      )
      private Integer connectionCount;
      @FieldReport(
         name = "elasticSearch.pool.threads.count",
         desc = "ElasticSearch集群线程池线程数"
      )
      private Integer threadsTotal;
      @FieldReport(
         name = "elasticSearch.pool.threads.active",
         desc = "ElasticSearch集群池线程活动线程数"
      )
      private Integer activateTotal;
      @FieldReport(
         name = "elasticSearch.pool.queue.size",
         desc = "ElasticSearch集群池线程队列大小"
      )
      private Integer queueSize;

      private ElasticSearchData() {
      }
   }
}
