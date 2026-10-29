package com.kuma.boot.flowengine.simpleflow.core.annotation;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.api.FlowEngine;
import com.kuma.boot.flowengine.simpleflow.api.model.StepDefinition;
import com.kuma.boot.flowengine.simpleflow.core.config.ThreadPoolConfig;
import com.kuma.boot.flowengine.simpleflow.core.engine.DefaultFlowEngine;
import com.kuma.boot.flowengine.simpleflow.core.executor.StandaloneStepExecutorRegistry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class AnnotationFlowConfigurationManager {
   private final AnnotationFlowProcessor processor;
   private final Map<String, Object> beanRegistry = new HashMap();
   private final ThreadPoolConfig threadPoolConfig;
   private FlowEngine flowEngine;
   private ExecutorService executorService;
   private boolean initialized = false;

   public AnnotationFlowConfigurationManager(String... basePackages) {
      this.processor = new AnnotationFlowProcessor(basePackages);
      this.threadPoolConfig = new ThreadPoolConfig();
   }

   public AnnotationFlowConfigurationManager(ThreadPoolConfig threadPoolConfig, String... basePackages) {
      this.processor = new AnnotationFlowProcessor(basePackages);
      this.threadPoolConfig = threadPoolConfig != null ? threadPoolConfig : new ThreadPoolConfig();
   }

   public FlowEngine initialize() {
      if (this.initialized) {
         LogUtils.warn("注解配置管理器已经初始化，跳过重复初始化", new Object[0]);
         return this.flowEngine;
      } else {
         LogUtils.info("开始初始化注解流程配置管理器", new Object[0]);

         try {
            this.initializeThreadPool();
            Map<String, com.kuma.boot.flowengine.simpleflow.api.model.FlowDefinition> flowDefinitions = this.processor.processAnnotations();
            this.flowEngine = new DefaultFlowEngine(this.executorService);

            for(Map.Entry<String, com.kuma.boot.flowengine.simpleflow.api.model.FlowDefinition> entry : flowDefinitions.entrySet()) {
               this.flowEngine.registerFlow((com.kuma.boot.flowengine.simpleflow.api.model.FlowDefinition)entry.getValue());
               LogUtils.info("注册流程定义: {}", new Object[]{entry.getKey()});
            }

            this.instantiateAndRegisterBeans(flowDefinitions);
            this.initialized = true;
            LogUtils.info("注解流程配置管理器初始化完成，共注册 {} 个流程定义", new Object[]{flowDefinitions.size()});
            return this.flowEngine;
         } catch (Exception e) {
            LogUtils.error("初始化注解流程配置管理器时发生错误", new Object[]{e});
            throw new RuntimeException("Failed to initialize annotation flow configuration manager", e);
         }
      }
   }

   private void initializeThreadPool() {
      ThreadFactory threadFactory = new ThreadFactory() {
         private final AtomicInteger threadNumber;

         {
            Objects.requireNonNull(AnnotationFlowConfigurationManager.this);
            this.threadNumber = new AtomicInteger(1);
         }

         public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "SimpleFlow-AnnotationConfig-" + this.threadNumber.getAndIncrement());
            thread.setDaemon(false);
            return thread;
         }
      };
      this.executorService = Executors.newFixedThreadPool(this.threadPoolConfig.getCoreSize(), threadFactory);
      LogUtils.info("初始化线程池，核心线程数: {}", new Object[]{this.threadPoolConfig.getCoreSize()});
   }

   private void instantiateAndRegisterBeans(Map<String, com.kuma.boot.flowengine.simpleflow.api.model.FlowDefinition> flowDefinitions) {
      Set<String> beanClassNames = new HashSet();

      for(com.kuma.boot.flowengine.simpleflow.api.model.FlowDefinition flowDefinition : flowDefinitions.values()) {
         if (flowDefinition.getSteps() != null) {
            for(StepDefinition step : flowDefinition.getSteps()) {
               if (step.getParameters() != null && step.getParameters().containsKey("bean")) {
                  String beanClassName = (String)step.getParameters().get("bean");
                  beanClassNames.add(beanClassName);
               }
            }
         }
      }

      for(String className : beanClassNames) {
         try {
            Class<?> clazz = Class.forName(className);
            Object instance = clazz.newInstance();
            this.beanRegistry.put(className, instance);
            String simpleName = clazz.getSimpleName();
            if (!this.beanRegistry.containsKey(simpleName)) {
               this.beanRegistry.put(simpleName, instance);
            }

            StandaloneStepExecutorRegistry.getInstance().registerBean(className, instance);
            StandaloneStepExecutorRegistry.getInstance().registerBean(simpleName, instance);

            for(com.kuma.boot.flowengine.simpleflow.api.model.FlowDefinition flowDef : flowDefinitions.values()) {
               if (flowDef.getSteps() != null && !flowDef.getSteps().isEmpty()) {
                  String firstStepBean = (String)((StepDefinition)flowDef.getSteps().get(0)).getParameters().get("bean");
                  if (className.equals(firstStepBean)) {
                     this.beanRegistry.put(flowDef.getId(), instance);
                     StandaloneStepExecutorRegistry.getInstance().registerBean(flowDef.getId(), instance);
                     LogUtils.debug("注册流程定义Bean: {} -> {}", new Object[]{flowDef.getId(), instance.getClass().getSimpleName()});
                  }
               }
            }

            LogUtils.debug("实例化并注册Bean: {} -> {}", new Object[]{className, instance.getClass().getSimpleName()});
         } catch (Exception e) {
            LogUtils.error("实例化Bean {} 时发生错误", new Object[]{className, e});
         }
      }

      LogUtils.info("共实例化并注册 {} 个Bean", new Object[]{this.beanRegistry.size()});
   }

   public void registerBean(String name, Object bean) {
      this.beanRegistry.put(name, bean);
      LogUtils.debug("手动注册Bean: {} -> {}", new Object[]{name, bean.getClass().getSimpleName()});
   }

   public Object getBean(String name) {
      return this.beanRegistry.get(name);
   }

   public <T> T getBean(Class<T> clazz) {
      Object bean = this.beanRegistry.get(clazz.getName());
      if (bean != null && clazz.isInstance(bean)) {
         return (T)bean;
      } else {
         bean = this.beanRegistry.get(clazz.getSimpleName());
         if (bean != null && clazz.isInstance(bean)) {
            return (T)bean;
         } else {
            for(Object registeredBean : this.beanRegistry.values()) {
               if (clazz.isInstance(registeredBean)) {
                  return (T)registeredBean;
               }
            }

            return null;
         }
      }
   }

   public boolean containsBean(String name) {
      return this.beanRegistry.containsKey(name);
   }

   public Set<String> getBeanNames() {
      return new HashSet(this.beanRegistry.keySet());
   }

   public FlowEngine getFlowEngine() {
      if (!this.initialized) {
         throw new IllegalStateException("Configuration manager not initialized. Call initialize() first.");
      } else {
         return this.flowEngine;
      }
   }

   public void destroy() {
      if (this.executorService != null && !this.executorService.isShutdown()) {
         this.executorService.shutdown();
         LogUtils.info("线程池已关闭", new Object[0]);
      }

      this.beanRegistry.clear();
      this.initialized = false;
      LogUtils.info("注解流程配置管理器已销毁", new Object[0]);
   }

   public ThreadPoolConfig getThreadPoolConfig() {
      return this.threadPoolConfig;
   }
}
