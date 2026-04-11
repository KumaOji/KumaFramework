package com.kuma.boot.flowengine.simpleflow.core.annotation;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.api.FlowEngine;
import com.kuma.boot.flowengine.simpleflow.api.model.FlowResult;
import com.kuma.boot.flowengine.simpleflow.core.config.ThreadPoolConfig;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AnnotationFlowStarter {
   public AnnotationFlowStarter() {
   }

   public static FlowEngine start(Class<?> mainClass) {
      String packageName = AnnotationFlowFactory.detectPackage(mainClass);
      LogUtils.info("\u542f\u52a8\u6ce8\u89e3\u6d41\u7a0b\uff0c\u626b\u63cf\u5305: {}", new Object[]{packageName});
      return AnnotationFlowFactory.initializeGlobal(packageName);
   }

   public static FlowEngine start(String... basePackages) {
      LogUtils.info("\u542f\u52a8\u6ce8\u89e3\u6d41\u7a0b\uff0c\u626b\u63cf\u5305: {}", new Object[]{Arrays.toString(basePackages)});
      return AnnotationFlowFactory.initializeGlobal(basePackages);
   }

   public static FlowEngine start(Class<?> mainClass, int corePoolSize, int maximumPoolSize, int queueCapacity) {
      String packageName = AnnotationFlowFactory.detectPackage(mainClass);
      ThreadPoolConfig config = AnnotationFlowFactory.createThreadPoolConfig(corePoolSize, maximumPoolSize, queueCapacity);
      LogUtils.info("\u542f\u52a8\u6ce8\u89e3\u6d41\u7a0b\uff0c\u626b\u63cf\u5305: {}\uff0c\u7ebf\u7a0b\u6c60\u914d\u7f6e: core={}, max={}, queue={}", new Object[]{packageName, corePoolSize, maximumPoolSize, queueCapacity});
      return AnnotationFlowFactory.initializeGlobal(config, packageName);
   }

   public static FlowEngine start(ThreadPoolConfig threadPoolConfig, String... basePackages) {
      LogUtils.info("\u542f\u52a8\u6ce8\u89e3\u6d41\u7a0b\uff0c\u626b\u63cf\u5305: {}\uff0c\u7ebf\u7a0b\u6c60\u914d\u7f6e: {}", new Object[]{Arrays.toString(basePackages), threadPoolConfig});
      return AnnotationFlowFactory.initializeGlobal(threadPoolConfig, basePackages);
   }

   public static void stop() {
      LogUtils.info("\u505c\u6b62\u6ce8\u89e3\u6d41\u7a0b", new Object[0]);
      AnnotationFlowFactory.destroyGlobal();
   }

   public static FlowEngine restart(Class<?> mainClass) {
      LogUtils.info("\u91cd\u542f\u6ce8\u89e3\u6d41\u7a0b", new Object[0]);
      stop();
      return start(mainClass);
   }

   public static FlowEngine restart(String... basePackages) {
      LogUtils.info("\u91cd\u542f\u6ce8\u89e3\u6d41\u7a0b", new Object[0]);
      stop();
      return start(basePackages);
   }

   public static FlowEngine getEngine() {
      return AnnotationFlowFactory.getGlobalFlowEngine();
   }

   public static boolean isStarted() {
      return AnnotationFlowFactory.isGlobalInitialized();
   }

   public static Object startAndRun(Class<?> mainClass, String flowId) {
      FlowEngine engine = start(mainClass);

      FlowResult var3;
      try {
         var3 = engine.execute((String)flowId, new HashMap());
      } finally {
         stop();
      }

      return var3;
   }

   public static Object startAndRun(Class<?> mainClass, String flowId, Object parameters) {
      FlowEngine engine = start(mainClass);

      try {
         if (parameters instanceof Map) {
            return engine.execute(flowId, (Map)parameters);
         } else {
            Map<String, Object> context = new HashMap();
            if (parameters != null) {
               context.put("input", parameters);
            }

            return engine.execute(flowId, context);
         }
      } catch (Exception e) {
         LogUtils.error("\u6267\u884c\u6d41\u7a0b\u5931\u8d25: {}", new Object[]{flowId, e});
         throw new RuntimeException("\u6267\u884c\u6d41\u7a0b\u5931\u8d25: " + flowId, e);
      }
   }

   public static void main(String[] args) {
      if (args.length == 0) {
         LogUtils.error("\u8bf7\u63d0\u4f9b\u4e3b\u7c7b\u540d\u4f5c\u4e3a\u53c2\u6570", new Object[0]);
         System.exit(1);
      }

      try {
         Class<?> mainClass = Class.forName(args[0]);
         FlowEngine engine = start(mainClass);
         LogUtils.info("\u6ce8\u89e3\u6d41\u7a0b\u5f15\u64ce\u542f\u52a8\u6210\u529f", new Object[0]);
         if (args.length > 1) {
            String flowId = args[1];
            LogUtils.info("\u6267\u884c\u6d41\u7a0b: {}", new Object[]{flowId});
            FlowResult result = engine.execute((String)flowId, new HashMap());
            LogUtils.info("\u6d41\u7a0b\u6267\u884c\u5b8c\u6210\uff0c\u7ed3\u679c: {}", new Object[]{result});
         }
      } catch (ClassNotFoundException e) {
         LogUtils.error("\u627e\u4e0d\u5230\u4e3b\u7c7b: {}", new Object[]{args[0], e});
         System.exit(1);
      } catch (Exception e) {
         LogUtils.error("\u542f\u52a8\u5931\u8d25", new Object[]{e});
         System.exit(1);
      }

   }

   public static void addShutdownHook() {
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
         LogUtils.info("\u5e94\u7528\u5173\u95ed\uff0c\u6e05\u7406\u6ce8\u89e3\u6d41\u7a0b\u8d44\u6e90", new Object[0]);
         stop();
      }));
   }

   public static FlowEngine startWithShutdownHook(Class<?> mainClass) {
      FlowEngine engine = start(mainClass);
      addShutdownHook();
      return engine;
   }

   public static FlowEngine startWithShutdownHook(String... basePackages) {
      FlowEngine engine = start(basePackages);
      addShutdownHook();
      return engine;
   }
}
