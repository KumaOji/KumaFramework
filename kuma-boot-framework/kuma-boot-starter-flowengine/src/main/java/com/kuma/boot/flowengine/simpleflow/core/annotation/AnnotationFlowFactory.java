package com.kuma.boot.flowengine.simpleflow.core.annotation;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.api.FlowEngine;
import com.kuma.boot.flowengine.simpleflow.core.config.ThreadPoolConfig;
import java.util.HashSet;
import java.util.Set;

public class AnnotationFlowFactory {
   private static AnnotationFlowConfigurationManager globalConfigManager;

   public AnnotationFlowFactory() {
   }

   public static AnnotationFlowConfigurationManager createConfigurationManager(String... basePackages) {
      return new AnnotationFlowConfigurationManager(basePackages);
   }

   public static AnnotationFlowConfigurationManager createConfigurationManager(ThreadPoolConfig threadPoolConfig, String... basePackages) {
      return new AnnotationFlowConfigurationManager(threadPoolConfig, basePackages);
   }

   public static FlowEngine createFlowEngine(String... basePackages) {
      AnnotationFlowConfigurationManager configManager = createConfigurationManager(basePackages);
      return configManager.initialize();
   }

   public static FlowEngine createFlowEngine(ThreadPoolConfig threadPoolConfig, String... basePackages) {
      AnnotationFlowConfigurationManager configManager = createConfigurationManager(threadPoolConfig, basePackages);
      return configManager.initialize();
   }

   public static void setGlobalConfigurationManager(AnnotationFlowConfigurationManager configManager) {
      globalConfigManager = configManager;
      LogUtils.info("设置全局注解流程配置管理器", new Object[0]);
   }

   public static AnnotationFlowConfigurationManager getGlobalConfigurationManager() {
      return globalConfigManager;
   }

   public static FlowEngine getGlobalFlowEngine() {
      if (globalConfigManager == null) {
         throw new IllegalStateException("全局配置管理器未设置，请先调用 setGlobalConfigurationManager");
      } else {
         return globalConfigManager.getFlowEngine();
      }
   }

   public static FlowEngine initializeGlobal(String... basePackages) {
      if (globalConfigManager != null) {
         LogUtils.warn("全局配置管理器已存在，将销毁现有配置并重新初始化", new Object[0]);
         globalConfigManager.destroy();
      }

      globalConfigManager = createConfigurationManager(basePackages);
      return globalConfigManager.initialize();
   }

   public static FlowEngine initializeGlobal(ThreadPoolConfig threadPoolConfig, String... basePackages) {
      if (globalConfigManager != null) {
         LogUtils.warn("全局配置管理器已存在，将销毁现有配置并重新初始化", new Object[0]);
         globalConfigManager.destroy();
      }

      globalConfigManager = createConfigurationManager(threadPoolConfig, basePackages);
      return globalConfigManager.initialize();
   }

   public static void destroyGlobal() {
      if (globalConfigManager != null) {
         globalConfigManager.destroy();
         globalConfigManager = null;
         LogUtils.info("全局注解流程配置已销毁", new Object[0]);
      }

   }

   public static boolean isGlobalInitialized() {
      return globalConfigManager != null;
   }

   public static String detectPackage(Class<?> clazz) {
      return clazz.getPackage().getName();
   }

   public static String[] detectPackages(Class<?>... classes) {
      Set<String> packages = new HashSet();

      for(Class<?> clazz : classes) {
         packages.add(clazz.getPackage().getName());
      }

      return (String[])packages.toArray(new String[0]);
   }

   public static ThreadPoolConfig createDefaultThreadPoolConfig() {
      ThreadPoolConfig config = new ThreadPoolConfig();
      config.setCoreSize(5);
      config.setMaxSize(20);
      config.setKeepAliveSeconds(60);
      config.setQueueCapacity(100);
      return config;
   }

   public static ThreadPoolConfig createThreadPoolConfig(int corePoolSize, int maximumPoolSize, int queueCapacity) {
      ThreadPoolConfig config = new ThreadPoolConfig();
      config.setCoreSize(corePoolSize);
      config.setMaxSize(maximumPoolSize);
      config.setQueueCapacity(queueCapacity);
      return config;
   }
}
