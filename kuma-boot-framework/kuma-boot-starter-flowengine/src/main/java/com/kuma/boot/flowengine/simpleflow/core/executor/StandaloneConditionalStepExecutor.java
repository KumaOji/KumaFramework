package com.kuma.boot.flowengine.simpleflow.core.executor;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.api.ConditionNode;
import com.kuma.boot.flowengine.simpleflow.api.ExecutableNode;
import com.kuma.boot.flowengine.simpleflow.api.FlowContext;
import com.kuma.boot.flowengine.simpleflow.api.StepExecutor;
import com.kuma.boot.flowengine.simpleflow.api.model.StepDefinition;
import com.kuma.boot.flowengine.simpleflow.api.model.StepResult;
import com.kuma.boot.flowengine.simpleflow.expression.ExpressionFactory;
import com.kuma.boot.flowengine.simpleflow.expression.api.ExpressionEngine;
import java.util.List;
import java.util.Map;

public class StandaloneConditionalStepExecutor implements StepExecutor {
   private final ExpressionEngine expressionEngine;

   public StandaloneConditionalStepExecutor() {
      this.expressionEngine = ExpressionFactory.getDefaultEngine();
   }

   public StandaloneConditionalStepExecutor(ExpressionEngine expressionEngine) {
      this.expressionEngine = expressionEngine;
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
               String condition = currentStep.getCondition();
               Map<String, Object> parameters = currentStep.getParameters();
               Map<String, Object> conditionalConfig = (Map)parameters.get("conditional");
               if (conditionalConfig == null) {
                  return StepResult.failure(currentStep.getId(), currentStep.getName(), "Conditional configuration is required");
               } else {
                  boolean conditionResult = this.evaluateCondition(condition, context);
                  LogUtils.info("Condition '{}' evaluated to: {}", new Object[]{condition, conditionResult});
                  List<StepDefinition> stepsToExecute = this.getStepsToExecute(conditionalConfig, conditionResult, context);
                  if (stepsToExecute != null && !stepsToExecute.isEmpty()) {
                     StepResult lastResult = null;

                     for(StepDefinition step : stepsToExecute) {
                        StepResult stepResult = this.executeStep(step, context);
                        if (!stepResult.isSuccess()) {
                           return stepResult;
                        }

                        lastResult = stepResult;
                     }

                     if (lastResult != null) {
                        return lastResult;
                     } else {
                        return StepResult.success(currentStep.getId(), currentStep.getName());
                     }
                  } else {
                     LogUtils.info("No steps to execute for condition result: {}", new Object[]{conditionResult});
                     return StepResult.success(currentStep.getId(), currentStep.getName());
                  }
               }
            }
         }
      } catch (Exception e) {
         LogUtils.error("Error executing conditional step", new Object[]{e});
         return StepResult.failure("unknown", "unknown", e);
      }
   }

   private boolean evaluateCondition(String condition, FlowContext context) {
      if (condition != null && !condition.trim().isEmpty()) {
         try {
            Object result = this.expressionEngine.evaluate(condition, context.getAll());
            if (result instanceof Boolean) {
               return (Boolean)result;
            } else {
               return result != null;
            }
         } catch (Exception e) {
            LogUtils.warn("Failed to evaluate condition '{}', defaulting to false", new Object[]{condition, e});
            return false;
         }
      } else {
         return true;
      }
   }

   private List<StepDefinition> getStepsToExecute(Map<String, Object> conditionalConfig, boolean conditionResult, FlowContext context) {
      List<Map<String, Object>> cases = (List)conditionalConfig.get("cases");
      if (cases != null && !cases.isEmpty()) {
         return this.getStepsFromCases(cases, context);
      } else {
         return conditionResult ? (List)conditionalConfig.get("trueSteps") : (List)conditionalConfig.get("falseSteps");
      }
   }

   private List<StepDefinition> getStepsFromCases(List<Map<String, Object>> cases, FlowContext context) {
      for(Map<String, Object> caseConfig : cases) {
         String caseCondition = (String)caseConfig.get("condition");
         if (this.evaluateCondition(caseCondition, context)) {
            return (List)caseConfig.get("steps");
         }
      }

      StepDefinition currentStep = (StepDefinition)context.get("currentStepDefinition").orElse((Object)null);
      if (currentStep == null) {
         return null;
      } else {
         Map<String, Object> conditionalConfig = (Map)currentStep.getParameters().get("conditional");
         return (List)conditionalConfig.get("defaultSteps");
      }
   }

   private StepResult executeStep(StepDefinition step, FlowContext context) {
      try {
         context.setCurrentStepId(step.getId());
         context.set("currentStepDefinition", step);
         String executorClass = step.getExecutorClass();
         if (executorClass == null) {
            executorClass = this.determineExecutorByBeanType(step);
         }

         Class<?> clazz = Class.forName(executorClass);
         StepExecutor executor = (StepExecutor)clazz.getDeclaredConstructor().newInstance();
         return executor.execute(context);
      } catch (Exception e) {
         LogUtils.error("Failed to execute step: {}", new Object[]{step.getId(), e});
         return StepResult.failure(step.getId(), step.getName(), e);
      }
   }

   private String determineExecutorByBeanType(StepDefinition stepDefinition) {
      Map<String, Object> parameters = stepDefinition.getParameters();
      if (parameters == null) {
         throw new IllegalArgumentException("Step parameters cannot be null for step: " + stepDefinition.getId());
      } else {
         String beanName = (String)parameters.get("bean");
         String nodeName = (String)parameters.get("node");
         if (nodeName != null && !nodeName.trim().isEmpty()) {
            return this.determineExecutorByNodeType(nodeName);
         } else if (beanName != null && !beanName.trim().isEmpty()) {
            return this.determineExecutorByBeanName(beanName);
         } else if (stepDefinition.getType() == StepDefinition.StepType.SERVICE) {
            return "com.kuma.boot.flowengine.simpleflow.core.executor.StandaloneBeanStepExecutor";
         } else if (stepDefinition.getType() == StepDefinition.StepType.CONDITIONAL) {
            return "com.kuma.boot.flowengine.simpleflow.core.executor.StandaloneConditionalStepExecutor";
         } else {
            throw new IllegalArgumentException("Unsupported step type: " + String.valueOf(stepDefinition.getType()));
         }
      }
   }

   private String determineExecutorByNodeType(String nodeName) {
      StandaloneStepExecutorRegistry registry = StandaloneStepExecutorRegistry.getInstance();
      if (registry.hasExecutableNode(nodeName)) {
         return "com.kuma.boot.flowengine.simpleflow.core.executor.ExecutableNodeStepExecutor";
      } else if (registry.hasConditionNode(nodeName)) {
         return "com.kuma.boot.flowengine.simpleflow.core.executor.ConditionNodeStepExecutor";
      } else {
         throw new IllegalArgumentException("Node not found in registry: " + nodeName);
      }
   }

   private String determineExecutorByBeanName(String beanName) {
      StandaloneStepExecutorRegistry registry = StandaloneStepExecutorRegistry.getInstance();
      Object beanInstance = registry.getBean(beanName);
      if (beanInstance == null) {
         throw new IllegalArgumentException("Bean not found in registry: " + beanName);
      } else if (beanInstance instanceof ExecutableNode) {
         return "com.kuma.boot.flowengine.simpleflow.core.executor.ExecutableNodeStepExecutor";
      } else {
         return beanInstance instanceof ConditionNode ? "com.kuma.boot.flowengine.simpleflow.core.executor.ConditionNodeStepExecutor" : "com.kuma.boot.flowengine.simpleflow.core.executor.StandaloneBeanStepExecutor";
      }
   }
}
