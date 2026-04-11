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
      LogUtils.info("\u8bbe\u7f6e\u5168\u5c40\u6ce8\u89e3\u6d41\u7a0b\u914d\u7f6e\u7ba1\u7406\u5668", new Object[0]);
   }

   public static AnnotationFlowConfigurationManager getGlobalConfigurationManager() {
      return globalConfigManager;
   }

   public static FlowEngine getGlobalFlowEngine() {
      if (globalConfigManager == null) {
         throw new IllegalStateException("\u5168\u5c40\u914d\u7f6e\u7ba1\u7406\u5668\u672a\u8bbe\u7f6e\uff0c\u8bf7\u5148\u8c03\u7528 setGlobalConfigurationManager");
      } else {
         return globalConfigManager.getFlowEngine();
      }
   }

   public static FlowEngine initializeGlobal(String... basePackages) {
      if (globalConfigManager != null) {
         LogUtils.warn("\u5168\u5c40\u914d\u7f6e\u7ba1\u7406\u5668\u5df2\u5b58\u5728\uff0c\u5c06\u9500\u6bc1\u73b0\u6709\u914d\u7f6e\u5e76\u91cd\u65b0\u521d\u59cb\u5316", new Object[0]);
         globalConfigManager.destroy();
      }

      globalConfigManager = createConfigurationManager(basePackages);
      return globalConfigManager.initialize();
   }

   public static FlowEngine initializeGlobal(ThreadPoolConfig threadPoolConfig, String... basePackages) {
      if (globalConfigManager != null) {
         LogUtils.warn("\u5168\u5c40\u914d\u7f6e\u7ba1\u7406\u5668\u5df2\u5b58\u5728\uff0c\u5c06\u9500\u6bc1\u73b0\u6709\u914d\u7f6e\u5e76\u91cd\u65b0\u521d\u59cb\u5316", new Object[0]);
         globalConfigManager.destroy();
      }

      globalConfigManager = createConfigurationManager(threadPoolConfig, basePackages);
      return globalConfigManager.initialize();
   }

   public static void destroyGlobal() {
      if (globalConfigManager != null) {
         globalConfigManager.destroy();
         globalConfigManager = null;
         LogUtils.info("\u5168\u5c40\u6ce8\u89e3\u6d41\u7a0b\u914d\u7f6e\u5df2\u9500\u6bc1", new Object[0]);
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
