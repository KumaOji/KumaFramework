package com.kuma.boot.flowengine.simpleflow.core.annotation;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.api.model.StepDefinition;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnnotationFlowProcessor {
   private final Set<String> basePackages;
   private final Map<String, com.kuma.boot.flowengine.simpleflow.api.model.FlowDefinition> flowDefinitions = new HashMap();

   public AnnotationFlowProcessor(String... basePackages) {
      this.basePackages = new HashSet(Arrays.asList(basePackages));
   }

   public Map<String, com.kuma.boot.flowengine.simpleflow.api.model.FlowDefinition> processAnnotations() {
      LogUtils.info("\u5f00\u59cb\u626b\u63cf\u6ce8\u89e3\u914d\u7f6e\uff0c\u626b\u63cf\u5305: {}", new Object[]{this.basePackages});

      for(String basePackage : this.basePackages) {
         this.scanPackage(basePackage);
      }

      LogUtils.info("\u6ce8\u89e3\u626b\u63cf\u5b8c\u6210\uff0c\u5171\u53d1\u73b0 {} \u4e2a\u6d41\u7a0b\u5b9a\u4e49", new Object[]{this.flowDefinitions.size()});
      return new HashMap(this.flowDefinitions);
   }

   private void scanPackage(String packageName) {
      try {
         String path = packageName.replace('.', '/');
         ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
         Enumeration<URL> resources = classLoader.getResources(path);

         while(resources.hasMoreElements()) {
            URL resource = (URL)resources.nextElement();
            File directory = new File(resource.getFile());
            if (directory.exists() && directory.isDirectory()) {
               this.scanDirectory(directory, packageName);
            }
         }
      } catch (Exception e) {
         LogUtils.error("\u626b\u63cf\u5305 {} \u65f6\u53d1\u751f\u9519\u8bef", new Object[]{packageName, e});
      }

   }

   private void scanDirectory(File directory, String packageName) {
      File[] files = directory.listFiles();
      if (files != null) {
         for(File file : files) {
            if (file.isDirectory()) {
               this.scanDirectory(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
               String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
               this.processClass(className);
            }
         }

      }
   }

   private void processClass(String className) {
      try {
         Class<?> clazz = Class.forName(className);
         if (clazz.isAnnotationPresent(FlowDefinition.class)) {
            this.processFlowDefinition(clazz);
         }
      } catch (Exception e) {
         LogUtils.debug("\u5904\u7406\u7c7b {} \u65f6\u53d1\u751f\u9519\u8bef: {}", new Object[]{className, e.getMessage()});
      }

   }

   private void processFlowDefinition(Class<?> clazz) {
      FlowDefinition flowAnnotation = (FlowDefinition)clazz.getAnnotation(FlowDefinition.class);
      String flowId = flowAnnotation.id().isEmpty() ? clazz.getSimpleName() : flowAnnotation.id();
      String flowName = flowAnnotation.name().isEmpty() ? clazz.getSimpleName() : flowAnnotation.name();
      LogUtils.info("\u53d1\u73b0\u6d41\u7a0b\u5b9a\u4e49: {} ({})", new Object[]{flowName, flowId});
      List<StepDefinition> steps = this.processSteps(clazz);
      Map<String, List<String>> dependencies = new HashMap();

      for(StepDefinition step : steps) {
         Map<String, Object> params = step.getParameters();
         if (params != null && params.containsKey("dependsOn")) {
            Object dependsOnObj = params.get("dependsOn");
            List<String> dependsOnList = new ArrayList();
            if (dependsOnObj instanceof String[]) {
               dependsOnList.addAll(Arrays.asList((String[])dependsOnObj));
            } else if (dependsOnObj instanceof String) {
               dependsOnList.add((String)dependsOnObj);
            }

            dependencies.put(step.getId(), dependsOnList);
         }
      }

      Map<String, Object> properties = new HashMap();
      properties.put("enableParallel", flowAnnotation.enableParallel());
      properties.put("maxParallelism", flowAnnotation.maxParallelism());
      properties.put("timeout", flowAnnotation.timeout());
      com.kuma.boot.flowengine.simpleflow.api.model.FlowDefinition flowDefinition = com.kuma.boot.flowengine.simpleflow.api.model.FlowDefinition.builder().id(flowId).name(flowName).description(flowAnnotation.description()).version(flowAnnotation.version()).steps(steps).dependencies(dependencies).properties(properties).build();
      this.flowDefinitions.put(flowId, flowDefinition);
   }

   private List<StepDefinition> processSteps(Class<?> clazz) {
      List<StepDefinition> steps = new ArrayList();
      Method[] methods = clazz.getDeclaredMethods();

      for(Method method : methods) {
         if (method.isAnnotationPresent(FlowStep.class)) {
            steps.add(this.processFlowStep(method, clazz));
         } else if (method.isAnnotationPresent(ConditionalStep.class)) {
            steps.add(this.processConditionalStep(method, clazz));
         }
      }

      steps.sort(Comparator.comparingInt((step) -> {
         Object order = step.getParameters().get("order");
         return order instanceof Integer ? (Integer)order : 0;
      }));
      return steps;
   }

   private StepDefinition processFlowStep(Method method, Class<?> clazz) {
      FlowStep stepAnnotation = (FlowStep)method.getAnnotation(FlowStep.class);
      String stepId = stepAnnotation.id().isEmpty() ? method.getName() : stepAnnotation.id();
      String stepName = stepAnnotation.name().isEmpty() ? method.getName() : stepAnnotation.name();
      Map<String, Object> parameters = new HashMap();
      parameters.put("bean", clazz.getName());
      parameters.put("method", method.getName());
      parameters.put("order", stepAnnotation.order());
      StepDefinition.StepDefinitionBuilder builder = StepDefinition.builder().id(stepId).name(stepName).description(stepAnnotation.description()).type(this.convertStepType(stepAnnotation.type())).timeoutMs(stepAnnotation.timeout()).maxRetries(stepAnnotation.retryCount()).retryDelayMs(stepAnnotation.retryInterval()).parameters(parameters);
      if (stepAnnotation.dependsOn().length > 0) {
         parameters.put("dependsOn", Arrays.asList(stepAnnotation.dependsOn()));
      }

      if (!stepAnnotation.condition().isEmpty()) {
         parameters.put("condition", stepAnnotation.condition());
      }

      if (stepAnnotation.async()) {
         parameters.put("async", true);
      }

      StepDefinition stepDefinition = builder.build();
      LogUtils.debug("\u5904\u7406\u6b65\u9aa4: {} ({})", new Object[]{stepName, stepId});
      return stepDefinition;
   }

   private StepDefinition processConditionalStep(Method method, Class<?> clazz) {
      ConditionalStep stepAnnotation = (ConditionalStep)method.getAnnotation(ConditionalStep.class);
      String stepId = stepAnnotation.id().isEmpty() ? method.getName() : stepAnnotation.id();
      String stepName = stepAnnotation.name().isEmpty() ? method.getName() : stepAnnotation.name();
      Map<String, Object> parameters = new HashMap();
      parameters.put("bean", clazz.getName());
      parameters.put("method", method.getName());
      parameters.put("order", stepAnnotation.order());
      if (stepAnnotation.onTrue().length > 0) {
         parameters.put("onTrue", Arrays.asList(stepAnnotation.onTrue()));
      }

      if (stepAnnotation.onFalse().length > 0) {
         parameters.put("onFalse", Arrays.asList(stepAnnotation.onFalse()));
      }

      StepDefinition.StepDefinitionBuilder builder = StepDefinition.builder().id(stepId).name(stepName).description(stepAnnotation.description()).type(StepDefinition.StepType.CONDITIONAL).timeoutMs(stepAnnotation.timeout()).maxRetries(stepAnnotation.retryCount()).retryDelayMs(stepAnnotation.retryInterval()).parameters(parameters);
      if (stepAnnotation.dependsOn().length > 0) {
         parameters.put("dependsOn", Arrays.asList(stepAnnotation.dependsOn()));
      }

      StepDefinition stepDefinition = builder.build();
      LogUtils.debug("\u5904\u7406\u6761\u4ef6\u6b65\u9aa4: {} ({})", new Object[]{stepName, stepId});
      return stepDefinition;
   }

   private StepDefinition.StepType convertStepType(FlowStep.StepType annotationType) {
      switch (annotationType) {
         case SERVICE -> {
            return StepDefinition.StepType.SERVICE;
         }
         case CONDITIONAL -> {
            return StepDefinition.StepType.CONDITIONAL;
         }
         case SIMPLE -> {
            return StepDefinition.StepType.SIMPLE;
         }
         default -> {
            return StepDefinition.StepType.SIMPLE;
         }
      }
   }
}
