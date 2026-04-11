package com.kuma.boot.flowengine.simpleflow.core.executor;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.api.FlowContext;
import com.kuma.boot.flowengine.simpleflow.api.StepExecutor;
import com.kuma.boot.flowengine.simpleflow.api.StepHandler;
import com.kuma.boot.flowengine.simpleflow.api.model.StepDefinition;
import com.kuma.boot.flowengine.simpleflow.api.model.StepResult;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StandaloneBeanStepExecutor implements StepExecutor {
   public StandaloneBeanStepExecutor() {
   }

   public StepResult execute(FlowContext context) {
      try {
         String currentStepId = (String)context.getCurrentStepId().orElse((Object)null);
         if (currentStepId == null) {
            return StepResult.failure("unknown", "unknown", "No current step ID found in context");
         } else {
            StepDefinition currentStep = (StepDefinition)context.get("currentStepDefinition").orElse((Object)null);
            if (currentStep == null) {
               return StepResult.failure(currentStepId, "unknown", "Current step definition not found in context");
            } else {
               Map<String, Object> parameters = currentStep.getParameters();
               String beanName = (String)parameters.get("bean");
               String methodName = (String)parameters.get("method");
               if (beanName != null && !beanName.trim().isEmpty()) {
                  if (methodName == null || methodName.trim().isEmpty()) {
                     Object beanInstance = StandaloneStepExecutorRegistry.getInstance().getBean(beanName);
                     if (beanInstance == null) {
                        return StepResult.failure(currentStep.getId(), currentStep.getName(), "Bean not found: " + beanName);
                     }

                     if (beanInstance instanceof StepHandler) {
                        LogUtils.info("Bean '{}' implements StepHandler interface, using default execute method", new Object[]{beanName});

                        try {
                           StepResult result = ((StepHandler)beanInstance).execute(context);
                           LogUtils.info("Successfully executed default method: {}.execute", new Object[]{beanName});
                           return result;
                        } catch (Exception e) {
                           LogUtils.error("Failed to execute default method: {}.execute", new Object[]{beanName, e});
                           return StepResult.failure(currentStep.getId(), currentStep.getName(), e);
                        }
                     }

                     methodName = "execute";
                     LogUtils.info("Bean '{}' does not implement StepHandler interface, trying default method: execute", new Object[]{beanName});
                  }

                  Object beanInstance = StandaloneStepExecutorRegistry.getInstance().getBean(beanName);
                  if (beanInstance == null) {
                     LogUtils.error("Bean not found: {}", new Object[]{beanName});
                     return StepResult.failure(currentStep.getId(), currentStep.getName(), "Bean not found: " + beanName);
                  } else {
                     LogUtils.info("Found bean instance: {} ({})", new Object[]{beanName, beanInstance.getClass().getName()});
                     LogUtils.info("About to invoke method: {} on bean: {}", new Object[]{methodName, beanName});
                     Object result = this.invokeMethod(beanInstance, methodName, context);
                     LogUtils.info("Successfully executed method: {}.{}, result: {}", new Object[]{beanName, methodName, result});
                     return StepResult.success(currentStep.getId(), currentStep.getName());
                  }
               } else {
                  return StepResult.failure(currentStep.getId(), currentStep.getName(), "Bean name is required");
               }
            }
         }
      } catch (Exception e) {
         LogUtils.error("Failed to execute bean step", new Object[]{e});
         return StepResult.failure("unknown", "unknown", e);
      }
   }

   private Object invokeMethod(Object beanInstance, String methodName, FlowContext context) throws Exception {
      if (beanInstance instanceof StepHandler) {
         LogUtils.debug("Bean implements StepHandler interface, using execute method", new Object[0]);
         return ((StepHandler)beanInstance).execute(context);
      } else if (methodName != null && !methodName.trim().isEmpty()) {
         Class<?> beanClass = beanInstance.getClass();
         Method[] methods = beanClass.getMethods();
         Method targetMethod = null;

         for(Method method : methods) {
            if (method.getName().equals(methodName)) {
               targetMethod = method;
               break;
            }
         }

         if (targetMethod == null) {
            throw new NoSuchMethodException("Method not found: " + methodName + " in class " + beanClass.getName());
         } else {
            Object[] args = this.prepareMethodArguments(targetMethod, context);
            LogUtils.info("Invoking method: {} on bean: {} with args: {}", new Object[]{methodName, beanInstance.getClass().getSimpleName(), args});
            targetMethod.setAccessible(true);
            Object result = targetMethod.invoke(beanInstance, args);
            LogUtils.info("Method {} executed successfully, result: {}", new Object[]{methodName, result});
            return result;
         }
      } else if (beanInstance instanceof StepHandler) {
         return ((StepHandler)beanInstance).execute(context);
      } else {
         throw new IllegalArgumentException("Method name is required for beans that don't implement StepHandler interface");
      }
   }

   private Object[] prepareMethodArguments(Method method, FlowContext context) {
      Parameter[] parameters = method.getParameters();
      if (parameters.length == 0) {
         return new Object[0];
      } else {
         List<Object> args = new ArrayList();

         for(Parameter parameter : parameters) {
            Class<?> paramType = parameter.getType();
            if (FlowContext.class.isAssignableFrom(paramType)) {
               args.add(context);
            } else if (Map.class.isAssignableFrom(paramType)) {
               args.add(context.getAll());
            } else if (String.class.isAssignableFrom(paramType)) {
               String paramName = parameter.getName();
               Object value = context.getAll().get(paramName);
               args.add(value != null ? value.toString() : null);
            } else if (!paramType.isPrimitive() && !Number.class.isAssignableFrom(paramType)) {
               String paramName = parameter.getName();
               Object value = context.getAll().get(paramName);
               args.add(value);
            } else {
               String paramName = parameter.getName();
               Object value = context.getAll().get(paramName);
               args.add(this.convertToType(value, paramType));
            }
         }

         return args.toArray();
      }
   }

   private Object convertToType(Object value, Class<?> targetType) {
      if (value == null) {
         return this.getDefaultValue(targetType);
      } else if (targetType.isAssignableFrom(value.getClass())) {
         return value;
      } else {
         String stringValue = value.toString();

         try {
            if (targetType != Integer.TYPE && targetType != Integer.class) {
               if (targetType != Long.TYPE && targetType != Long.class) {
                  if (targetType != Double.TYPE && targetType != Double.class) {
                     if (targetType != Float.TYPE && targetType != Float.class) {
                        return targetType != Boolean.TYPE && targetType != Boolean.class ? value : Boolean.parseBoolean(stringValue);
                     } else {
                        return Float.parseFloat(stringValue);
                     }
                  } else {
                     return Double.parseDouble(stringValue);
                  }
               } else {
                  return Long.parseLong(stringValue);
               }
            } else {
               return Integer.parseInt(stringValue);
            }
         } catch (NumberFormatException var5) {
            LogUtils.warn("Failed to convert value '{}' to type {}, using default value", new Object[]{value, targetType.getName()});
            return this.getDefaultValue(targetType);
         }
      }
   }

   private Object getDefaultValue(Class<?> type) {
      if (type != Integer.TYPE && type != Integer.class) {
         if (type != Long.TYPE && type != Long.class) {
            if (type != Double.TYPE && type != Double.class) {
               if (type != Float.TYPE && type != Float.class) {
                  return type != Boolean.TYPE && type != Boolean.class ? null : false;
               } else {
                  return 0.0F;
               }
            } else {
               return (double)0.0F;
            }
         } else {
            return 0L;
         }
      } else {
         return 0;
      }
   }
}
