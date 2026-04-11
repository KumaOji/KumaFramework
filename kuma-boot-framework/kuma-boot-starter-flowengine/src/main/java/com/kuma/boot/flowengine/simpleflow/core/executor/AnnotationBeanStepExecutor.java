package com.kuma.boot.flowengine.simpleflow.core.executor;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.api.FlowContext;
import com.kuma.boot.flowengine.simpleflow.api.StepExecutor;
import com.kuma.boot.flowengine.simpleflow.api.model.StepDefinition;
import com.kuma.boot.flowengine.simpleflow.api.model.StepResult;
import com.kuma.boot.flowengine.simpleflow.core.annotation.AnnotationFlowConfigurationManager;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class AnnotationBeanStepExecutor implements StepExecutor {
   private final AnnotationFlowConfigurationManager configurationManager;

   public AnnotationBeanStepExecutor(AnnotationFlowConfigurationManager configurationManager) {
      this.configurationManager = configurationManager;
   }

   public StepResult execute(FlowContext context) {
      LocalDateTime startTime = LocalDateTime.now();
      LogUtils.debug("\u5f00\u59cb\u6267\u884c\u6ce8\u89e3Bean\u6b65\u9aa4", new Object[0]);

      try {
         String currentStepId = (String)context.getCurrentStepId().orElse((Object)null);
         if (currentStepId == null) {
            return StepResult.failure("unknown", "unknown", "No current step ID found in context");
         } else {
            StepDefinition stepDefinition = (StepDefinition)context.get("currentStepDefinition").orElse((Object)null);
            if (stepDefinition == null) {
               return StepResult.failure(currentStepId, "unknown", "Current step definition not found in context");
            } else {
               Map<String, Object> parameters = stepDefinition.getParameters();
               String beanName = (String)parameters.get("bean");
               String methodName = (String)parameters.get("method");
               if (beanName != null && methodName != null) {
                  Object beanInstance = this.getBeanInstance(beanName);
                  if (beanInstance == null) {
                     return StepResult.failure(stepDefinition.getId(), stepDefinition.getName(), "\u627e\u4e0d\u5230Bean: " + beanName);
                  } else {
                     Method method = this.findMethod(beanInstance.getClass(), methodName);
                     if (method == null) {
                        return StepResult.failure(stepDefinition.getId(), stepDefinition.getName(), "\u627e\u4e0d\u5230\u65b9\u6cd5: " + methodName);
                     } else {
                        Object methodResult = this.invokeMethod(beanInstance, method, context);
                        if (methodResult != null) {
                           context.set(stepDefinition.getId() + "_result", methodResult);
                        }

                        LocalDateTime endTime = LocalDateTime.now();
                        LogUtils.info("\u6ce8\u89e3Bean\u6b65\u9aa4\u6267\u884c\u6210\u529f: {}", new Object[]{stepDefinition.getId()});
                        Map<String, Object> outputData = new HashMap();
                        if (methodResult != null) {
                           outputData.put("result", methodResult);
                        }

                        return StepResult.builder().stepId(stepDefinition.getId()).stepName(stepDefinition.getName()).status(StepResult.Status.SUCCESS).startTime(startTime).endTime(endTime).durationMs(Duration.between(startTime, endTime).toMillis()).outputData(outputData).executorName(this.getClass().getSimpleName()).build();
                     }
                  }
               } else {
                  return StepResult.failure(stepDefinition.getId(), stepDefinition.getName(), "Bean\u540d\u79f0\u548c\u65b9\u6cd5\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a");
               }
            }
         }
      } catch (Exception e) {
         LocalDateTime endTime = LocalDateTime.now();
         LogUtils.error("\u6ce8\u89e3Bean\u6b65\u9aa4\u6267\u884c\u5931\u8d25", new Object[]{e});
         return StepResult.builder().stepId("unknown").stepName("unknown").status(StepResult.Status.FAILED).startTime(startTime).endTime(endTime).durationMs(Duration.between(startTime, endTime).toMillis()).error(e).errorMessage(e.getMessage()).executorName(this.getClass().getSimpleName()).build();
      }
   }

   private Object getBeanInstance(String beanName) {
      Object bean = this.configurationManager.getBean(beanName);
      if (bean != null) {
         return bean;
      } else {
         try {
            Class<?> clazz = Class.forName(beanName);
            return this.configurationManager.getBean(clazz);
         } catch (ClassNotFoundException var4) {
            LogUtils.debug("\u65e0\u6cd5\u627e\u5230\u7c7b: {}", new Object[]{beanName});
            return null;
         }
      }
   }

   private Method findMethod(Class<?> clazz, String methodName) {
      Method[] methods = clazz.getDeclaredMethods();

      for(Method method : methods) {
         if (method.getName().equals(methodName) && method.getParameterCount() == 0) {
            method.setAccessible(true);
            return method;
         }
      }

      for(Method method : methods) {
         if (method.getName().equals(methodName) && method.getParameterCount() == 1) {
            Class<?>[] paramTypes = method.getParameterTypes();
            if (FlowContext.class.isAssignableFrom(paramTypes[0])) {
               method.setAccessible(true);
               return method;
            }
         }
      }

      for(Method method : methods) {
         if (method.getName().equals(methodName) && method.getParameterCount() == 1) {
            Class<?>[] paramTypes = method.getParameterTypes();
            if (Map.class.isAssignableFrom(paramTypes[0])) {
               method.setAccessible(true);
               return method;
            }
         }
      }

      return null;
   }

   private Object invokeMethod(Object bean, Method method, FlowContext context) throws Exception {
      int paramCount = method.getParameterCount();
      if (paramCount == 0) {
         return method.invoke(bean);
      } else {
         if (paramCount == 1) {
            Class<?> paramType = method.getParameterTypes()[0];
            if (FlowContext.class.isAssignableFrom(paramType)) {
               return method.invoke(bean, context);
            }

            if (Map.class.isAssignableFrom(paramType)) {
               Map<String, Object> variables = new HashMap();
               if (context != null) {
                  variables.putAll(context.getAll());
               }

               return method.invoke(bean, variables);
            }
         }

         throw new IllegalArgumentException("\u4e0d\u652f\u6301\u7684\u65b9\u6cd5\u7b7e\u540d: " + method.getName());
      }
   }

   public boolean supports(StepDefinition step) {
      Map<String, Object> parameters = step.getParameters();
      return parameters != null && parameters.containsKey("bean") && parameters.containsKey("method");
   }
}
