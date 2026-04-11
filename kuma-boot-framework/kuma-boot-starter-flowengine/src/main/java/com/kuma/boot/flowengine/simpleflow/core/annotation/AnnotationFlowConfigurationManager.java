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
         LogUtils.warn("\u6ce8\u89e3\u914d\u7f6e\u7ba1\u7406\u5668\u5df2\u7ecf\u521d\u59cb\u5316\uff0c\u8df3\u8fc7\u91cd\u590d\u521d\u59cb\u5316", new Object[0]);
         return this.flowEngine;
      } else {
         LogUtils.info("\u5f00\u59cb\u521d\u59cb\u5316\u6ce8\u89e3\u6d41\u7a0b\u914d\u7f6e\u7ba1\u7406\u5668", new Object[0]);

         try {
            this.initializeThreadPool();
            Map<String, com.kuma.boot.flowengine.simpleflow.api.model.FlowDefinition> flowDefinitions = this.processor.processAnnotations();
            this.flowEngine = new DefaultFlowEngine(this.executorService);

            for(Map.Entry<String, com.kuma.boot.flowengine.simpleflow.api.model.FlowDefinition> entry : flowDefinitions.entrySet()) {
               this.flowEngine.registerFlow((com.kuma.boot.flowengine.simpleflow.api.model.FlowDefinition)entry.getValue());
               LogUtils.info("\u6ce8\u518c\u6d41\u7a0b\u5b9a\u4e49: {}", new Object[]{entry.getKey()});
            }

            this.instantiateAndRegisterBeans(flowDefinitions);
            this.initialized = true;
            LogUtils.info("\u6ce8\u89e3\u6d41\u7a0b\u914d\u7f6e\u7ba1\u7406\u5668\u521d\u59cb\u5316\u5b8c\u6210\uff0c\u5171\u6ce8\u518c {} \u4e2a\u6d41\u7a0b\u5b9a\u4e49", new Object[]{flowDefinitions.size()});
            return this.flowEngine;
         } catch (Exception e) {
            LogUtils.error("\u521d\u59cb\u5316\u6ce8\u89e3\u6d41\u7a0b\u914d\u7f6e\u7ba1\u7406\u5668\u65f6\u53d1\u751f\u9519\u8bef", new Object[]{e});
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
      LogUtils.info("\u521d\u59cb\u5316\u7ebf\u7a0b\u6c60\uff0c\u6838\u5fc3\u7ebf\u7a0b\u6570: {}", new Object[]{this.threadPoolConfig.getCoreSize()});
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
                     LogUtils.debug("\u6ce8\u518c\u6d41\u7a0b\u5b9a\u4e49Bean: {} -> {}", new Object[]{flowDef.getId(), instance.getClass().getSimpleName()});
                  }
               }
            }

            LogUtils.debug("\u5b9e\u4f8b\u5316\u5e76\u6ce8\u518cBean: {} -> {}", new Object[]{className, instance.getClass().getSimpleName()});
         } catch (Exception e) {
            LogUtils.error("\u5b9e\u4f8b\u5316Bean {} \u65f6\u53d1\u751f\u9519\u8bef", new Object[]{className, e});
         }
      }

      LogUtils.info("\u5171\u5b9e\u4f8b\u5316\u5e76\u6ce8\u518c {} \u4e2aBean", new Object[]{this.beanRegistry.size()});
   }

   public void registerBean(String name, Object bean) {
      this.beanRegistry.put(name, bean);
      LogUtils.debug("\u624b\u52a8\u6ce8\u518cBean: {} -> {}", new Object[]{name, bean.getClass().getSimpleName()});
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
         LogUtils.info("\u7ebf\u7a0b\u6c60\u5df2\u5173\u95ed", new Object[0]);
      }

      this.beanRegistry.clear();
      this.initialized = false;
      LogUtils.info("\u6ce8\u89e3\u6d41\u7a0b\u914d\u7f6e\u7ba1\u7406\u5668\u5df2\u9500\u6bc1", new Object[0]);
   }

   public ThreadPoolConfig getThreadPoolConfig() {
      return this.threadPoolConfig;
   }
}
