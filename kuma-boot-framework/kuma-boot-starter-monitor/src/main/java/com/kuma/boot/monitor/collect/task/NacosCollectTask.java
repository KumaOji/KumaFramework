package com.kuma.boot.monitor.collect.task;

import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import com.alibaba.nacos.client.naming.NacosNamingService;
import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.model.Lazy;
import com.kuma.boot.common.utils.common.PropertyUtils;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.reflect.ClassUtils;
import com.kuma.boot.common.utils.reflect.ReflectionUtils;
import com.kuma.boot.core.support.Collector;
import com.kuma.boot.monitor.annotation.FieldReport;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.collect.AbstractCollectTask;
import com.kuma.boot.monitor.collect.CollectInfo;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NacosCollectTask extends AbstractCollectTask {
   private static final String TASK_NAME = "kmc.monitor.collect.nacos";
   private final CollectTaskProperties collectTaskProperties;
   private final Lazy<Boolean> lazy = Lazy.of(() -> ClassUtils.isExist("com.alibaba.cloud.nacos.NacosServiceManager"));

   public NacosCollectTask(CollectTaskProperties collectTaskProperties) {
      this.collectTaskProperties = collectTaskProperties;
   }

   public int getTimeSpan() {
      return this.collectTaskProperties.getNacosTimeSpan();
   }

   public String getDesc() {
      return this.getClass().getName();
   }

   public String getName() {
      return "kmc.monitor.collect.nacos";
   }

   public boolean getEnabled() {
      return this.collectTaskProperties.isNacosEnabled() && Boolean.TRUE.equals(this.lazy.get());
   }

   protected CollectInfo getData() {
      try {
         Collector collector = Collector.getCollector();
         Object nacosServiceManager = ContextUtils.getBean("com.alibaba.cloud.nacos.NacosServiceManager", false);
         if (Objects.nonNull(collector) && Objects.nonNull(nacosServiceManager)) {
            NacosClientInfo info = new NacosClientInfo();

            try {
               NamingService namingService = (NamingService)ReflectionUtils.getFieldValue(nacosServiceManager, "namingService");
               NacosNamingService nacosNamingService = (NacosNamingService)namingService;
               info.namespace = (String)ReflectionUtils.getFieldValue(nacosNamingService, "namespace");
               info.logName = (String)ReflectionUtils.getFieldValue(nacosNamingService, "logName");
               info.instances = nacosNamingService.getAllInstances(PropertyUtils.getProperty(CommonConstants.SPRING_APP_NAME_KEY), CommonConstants.SPRING_APP_NAME_KEY);
               info.servicesOfServer = nacosNamingService.getServicesOfServer(0, Integer.MAX_VALUE);
               info.subscribeServices = nacosNamingService.getSubscribeServices();
               info.serverStatus = nacosNamingService.getServerStatus();
            } catch (Exception var6) {
            }

            return info;
         }
      } catch (Exception e) {
         if (LogUtils.isErrorEnabled()) {
            LogUtils.error(e);
         }
      }

      return null;
   }

   private static class NacosClientInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.nacos.namespace",
         desc = "nacos namespace"
      )
      private String namespace = "";
      @FieldReport(
         name = "kmc.monitor.collect.nacos.endpoint",
         desc = "nacos endpoint"
      )
      private String endpoint = "";
      @FieldReport(
         name = "kmc.monitor.collect.nacos.serverList",
         desc = "nacos serverList"
      )
      private String serverList = "";
      @FieldReport(
         name = "kmc.monitor.collect.nacos.cacheDir",
         desc = "nacos cacheDir"
      )
      private String cacheDir = "";
      @FieldReport(
         name = "kmc.monitor.collect.nacos.logName",
         desc = "nacos logName"
      )
      private String logName = "";
      @FieldReport(
         name = "kmc.monitor.collect.nacos.serverStatus",
         desc = "nacos serverStatus"
      )
      private String serverStatus = "";
      @FieldReport(
         name = "kmc.monitor.collect.nacos.instances",
         desc = "nacos instances"
      )
      private List<Instance> instances;
      @FieldReport(
         name = "kmc.monitor.collect.nacos.serviceInfoMap",
         desc = "nacos serviceInfoMap"
      )
      private Map<String, ServiceInfo> serviceInfoMap;
      @FieldReport(
         name = "kmc.monitor.collect.nacos.servicesOfServer",
         desc = "nacos servicesOfServer"
      )
      private ListView<String> servicesOfServer;
      @FieldReport(
         name = "kmc.monitor.collect.nacos.subscribeServices",
         desc = "nacos subscribeServices"
      )
      private List<ServiceInfo> subscribeServices;

      private NacosClientInfo() {
      }
   }
}
