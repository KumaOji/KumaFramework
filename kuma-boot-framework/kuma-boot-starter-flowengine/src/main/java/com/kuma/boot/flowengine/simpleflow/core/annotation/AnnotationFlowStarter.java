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
      LogUtils.info("启动注解流程，扫描包: {}", new Object[]{packageName});
      return AnnotationFlowFactory.initializeGlobal(packageName);
   }

   public static FlowEngine start(String... basePackages) {
      LogUtils.info("启动注解流程，扫描包: {}", new Object[]{Arrays.toString(basePackages)});
      return AnnotationFlowFactory.initializeGlobal(basePackages);
   }

   public static FlowEngine start(Class<?> mainClass, int corePoolSize, int maximumPoolSize, int queueCapacity) {
      String packageName = AnnotationFlowFactory.detectPackage(mainClass);
      ThreadPoolConfig config = AnnotationFlowFactory.createThreadPoolConfig(corePoolSize, maximumPoolSize, queueCapacity);
      LogUtils.info("启动注解流程，扫描包: {}，线程池配置: core={}, max={}, queue={}", new Object[]{packageName, corePoolSize, maximumPoolSize, queueCapacity});
      return AnnotationFlowFactory.initializeGlobal(config, packageName);
   }

   public static FlowEngine start(ThreadPoolConfig threadPoolConfig, String... basePackages) {
      LogUtils.info("启动注解流程，扫描包: {}，线程池配置: {}", new Object[]{Arrays.toString(basePackages), threadPoolConfig});
      return AnnotationFlowFactory.initializeGlobal(threadPoolConfig, basePackages);
   }

   public static void stop() {
      LogUtils.info("停止注解流程", new Object[0]);
      AnnotationFlowFactory.destroyGlobal();
   }

   public static FlowEngine restart(Class<?> mainClass) {
      LogUtils.info("重启注解流程", new Object[0]);
      stop();
      return start(mainClass);
   }

   public static FlowEngine restart(String... basePackages) {
      LogUtils.info("重启注解流程", new Object[0]);
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
         LogUtils.error("执行流程失败: {}", new Object[]{flowId, e});
         throw new RuntimeException("执行流程失败: " + flowId, e);
      }
   }

   public static void main(String[] args) {
      if (args.length == 0) {
         LogUtils.error("请提供主类名作为参数", new Object[0]);
         System.exit(1);
      }

      try {
         Class<?> mainClass = Class.forName(args[0]);
         FlowEngine engine = start(mainClass);
         LogUtils.info("注解流程引擎启动成功", new Object[0]);
         if (args.length > 1) {
            String flowId = args[1];
            LogUtils.info("执行流程: {}", new Object[]{flowId});
            FlowResult result = engine.execute((String)flowId, new HashMap());
            LogUtils.info("流程执行完成，结果: {}", new Object[]{result});
         }
      } catch (ClassNotFoundException e) {
         LogUtils.error("找不到主类: {}", new Object[]{args[0], e});
         System.exit(1);
      } catch (Exception e) {
         LogUtils.error("启动失败", new Object[]{e});
         System.exit(1);
      }

   }

   public static void addShutdownHook() {
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
         LogUtils.info("应用关闭，清理注解流程资源", new Object[0]);
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
