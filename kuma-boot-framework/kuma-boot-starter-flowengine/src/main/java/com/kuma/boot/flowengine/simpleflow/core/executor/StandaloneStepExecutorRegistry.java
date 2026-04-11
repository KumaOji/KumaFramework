package com.kuma.boot.flowengine.simpleflow.core.executor;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.api.ConditionNode;
import com.kuma.boot.flowengine.simpleflow.api.ExecutableNode;
import com.kuma.boot.flowengine.simpleflow.api.ServiceRegistry;
import com.kuma.boot.flowengine.simpleflow.core.registry.DefaultServiceRegistry;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StandaloneStepExecutorRegistry {
   private static final StandaloneStepExecutorRegistry INSTANCE = new StandaloneStepExecutorRegistry();
   private final Map<String, Class<?>> executorClasses = new ConcurrentHashMap();
   private final Map<String, Object> beanInstances = new ConcurrentHashMap();
   private final Map<String, ExecutableNode> executableNodes = new ConcurrentHashMap();
   private final Map<String, ConditionNode> conditionNodes = new ConcurrentHashMap();
   private final ServiceRegistry serviceRegistry = DefaultServiceRegistry.getInstance();

   private StandaloneStepExecutorRegistry() {
      this.registerDefaultExecutors();
   }

   public static StandaloneStepExecutorRegistry getInstance() {
      return INSTANCE;
   }

   private void registerDefaultExecutors() {
      try {
         this.registerExecutor("SERVICE", Class.forName("com.kuma.boot.flowengine.simpleflow.core.executor.StandaloneBeanStepExecutor"));
         this.registerExecutor("CONDITIONAL", Class.forName("com.kuma.boot.flowengine.simpleflow.core.executor.StandaloneConditionalStepExecutor"));
         this.registerExecutor("EXECUTABLE_NODE", Class.forName("com.kuma.boot.flowengine.simpleflow.core.executor.ExecutableNodeStepExecutor"));
         this.registerExecutor("CONDITION_NODE", Class.forName("com.kuma.boot.flowengine.simpleflow.core.executor.ConditionNodeStepExecutor"));
         this.registerExecutor("ANNOTATION_BEAN", Class.forName("com.kuma.boot.flowengine.simpleflow.core.executor.AnnotationBeanStepExecutor"));
         LogUtils.info("Default step executors registered successfully", new Object[0]);
      } catch (ClassNotFoundException e) {
         LogUtils.warn("Some default step executors not found, will be registered when available", new Object[]{e});
      }

   }

   public void registerExecutor(String stepType, Class<?> executorClass) {
      this.executorClasses.put(stepType, executorClass);
      LogUtils.info("Registered step executor for type: {} -> {}", new Object[]{stepType, executorClass.getName()});
   }

   public void registerBean(String beanName, Object beanInstance) {
      if (beanName != null && !beanName.trim().isEmpty()) {
         if (beanInstance == null) {
            throw new IllegalArgumentException("Bean instance cannot be null");
         } else {
            this.beanInstances.put(beanName, beanInstance);
            this.serviceRegistry.registerService(beanName, beanInstance);
            LogUtils.info("Registered bean: {} -> {}", new Object[]{beanName, beanInstance.getClass().getName()});
         }
      } else {
         throw new IllegalArgumentException("Bean name cannot be null or empty");
      }
   }

   public Object getBean(String beanName) {
      Optional<Object> service = this.serviceRegistry.getService(beanName);
      return service.isPresent() ? service.get() : this.beanInstances.get(beanName);
   }

   public ServiceRegistry getServiceRegistry() {
      return this.serviceRegistry;
   }

   public boolean hasBean(String beanName) {
      return this.beanInstances.containsKey(beanName);
   }

   public Object createExecutor(String stepType) {
      Class<?> executorClass = (Class)this.executorClasses.get(stepType);
      if (executorClass == null) {
         throw new IllegalArgumentException("No executor registered for step type: " + stepType);
      } else {
         try {
            Constructor<?> constructor = executorClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
         } catch (Exception e) {
            LogUtils.error("Failed to create executor instance for type: {}", new Object[]{stepType, e});
            throw new RuntimeException("Failed to create executor instance", e);
         }
      }
   }

   public Set<String> getRegisteredExecutorTypes() {
      return this.executorClasses.keySet();
   }

   public Set<String> getRegisteredBeanNames() {
      return this.beanInstances.keySet();
   }

   public void registerExecutableNode(String nodeName, ExecutableNode nodeInstance) {
      if (nodeName != null && !nodeName.trim().isEmpty() && nodeInstance != null) {
         this.executableNodes.put(nodeName, nodeInstance);
         LogUtils.info("ExecutableNode registered: {} -> {}", new Object[]{nodeName, nodeInstance.getClass().getSimpleName()});
      }

   }

   public ExecutableNode getExecutableNode(String nodeName) {
      return (ExecutableNode)this.executableNodes.get(nodeName);
   }

   public void registerConditionNode(String nodeName, ConditionNode nodeInstance) {
      if (nodeName != null && !nodeName.trim().isEmpty() && nodeInstance != null) {
         this.conditionNodes.put(nodeName, nodeInstance);
         LogUtils.info("ConditionNode registered: {} -> {}", new Object[]{nodeName, nodeInstance.getClass().getSimpleName()});
      }

   }

   public ConditionNode getConditionNode(String nodeName) {
      return (ConditionNode)this.conditionNodes.get(nodeName);
   }

   public boolean hasExecutableNode(String nodeName) {
      return this.executableNodes.containsKey(nodeName);
   }

   public boolean hasConditionNode(String nodeName) {
      return this.conditionNodes.containsKey(nodeName);
   }

   public Set<String> getRegisteredExecutableNodeNames() {
      return Collections.unmodifiableSet(this.executableNodes.keySet());
   }

   public Set<String> getRegisteredConditionNodeNames() {
      return Collections.unmodifiableSet(this.conditionNodes.keySet());
   }

   public void clear() {
      this.executorClasses.clear();
      this.beanInstances.clear();
      this.executableNodes.clear();
      this.conditionNodes.clear();
      this.registerDefaultExecutors();
      LogUtils.info("Registry cleared and default executors re-registered", new Object[0]);
   }
}
