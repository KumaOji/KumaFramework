package com.kuma.boot.flowengine.simpleflow.core.config;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.api.model.FlowDefinition;
import com.kuma.boot.flowengine.simpleflow.api.model.StepDefinition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StandaloneFlowConfigurationParser {
   public StandaloneFlowConfigurationParser() {
   }

   public FlowDefinition parseFlow(Map<String, Object> flowConfig) {
      String id = (String)flowConfig.get("id");
      String name = (String)flowConfig.get("name");
      String description = (String)flowConfig.get("description");
      String version = (String)flowConfig.get("version");
      if (id != null && !id.trim().isEmpty()) {
         List<Map<String, Object>> stepsConfig = (List)flowConfig.get("steps");
         if (stepsConfig != null && !stepsConfig.isEmpty()) {
            String threadPoolName = (String)flowConfig.get("thread-pool-name");
            Boolean sync = (Boolean)flowConfig.get("sync");
            boolean syncValue = sync != null ? sync : true;
            List<StepDefinition> steps = new ArrayList();

            for(Map<String, Object> stepConfig : stepsConfig) {
               StepDefinition step = this.parseStep(stepConfig);
               steps.add(step);
            }

            FlowDefinition definition = FlowDefinition.builder().id(id).name(name != null ? name : id).description(description).version(version != null ? version : "1.0").steps(steps).threadPoolName(threadPoolName).sync(syncValue).build();
            LogUtils.info("\u89e3\u6790\u6d41\u7a0b\u914d\u7f6e: id={}, threadPoolName={}, sync={}", new Object[]{id, threadPoolName, syncValue});
            return definition;
         } else {
            throw new IllegalArgumentException("Flow steps are required for flow: " + id);
         }
      } else {
         throw new IllegalArgumentException("Flow id is required");
      }
   }

   private StepDefinition parseStep(Map<String, Object> stepConfig) {
      String id = (String)stepConfig.get("id");
      String name = (String)stepConfig.get("name");
      String type = (String)stepConfig.get("type");
      String condition = (String)stepConfig.get("condition");
      String bean = (String)stepConfig.get("bean");
      String method = (String)stepConfig.get("method");
      if (id != null && !id.trim().isEmpty()) {
         if (type != null && !type.trim().isEmpty()) {
            Map<String, Object> parameters = new HashMap();
            if (bean != null) {
               parameters.put("bean", bean);
            }

            if (method != null) {
               parameters.put("method", method);
            }

            Map<String, Object> conditionalConfig = (Map)stepConfig.get("conditional");
            if (conditionalConfig != null) {
               parameters.put("conditional", this.parseConditionalConfig(conditionalConfig));
            }

            StepDefinition.StepType stepType = this.parseStepType(type);
            String executorClass = null;
            if (stepType == StepDefinition.StepType.SERVICE) {
               executorClass = "com.kuma.boot.flowengine.simpleflow.core.executor.StandaloneBeanStepExecutor";
            } else if (stepType == StepDefinition.StepType.CONDITIONAL) {
               executorClass = "com.kuma.boot.flowengine.simpleflow.core.executor.StandaloneConditionalStepExecutor";
            }

            return StepDefinition.builder().id(id).name(name != null ? name : id).type(stepType).condition(condition).parameters(parameters).executorClass(executorClass).build();
         } else {
            throw new IllegalArgumentException("Step type is required for step: " + id);
         }
      } else {
         throw new IllegalArgumentException("Step id is required");
      }
   }

   private Map<String, Object> parseConditionalConfig(Map<String, Object> conditionalConfig) {
      Map<String, Object> result = new HashMap();
      List<Map<String, Object>> trueStepsConfig = (List)conditionalConfig.get("trueSteps");
      if (trueStepsConfig != null) {
         List<StepDefinition> trueSteps = new ArrayList();

         for(Map<String, Object> stepConfig : trueStepsConfig) {
            trueSteps.add(this.parseStep(stepConfig));
         }

         result.put("trueSteps", trueSteps);
      }

      List<Map<String, Object>> falseStepsConfig = (List)conditionalConfig.get("falseSteps");
      if (falseStepsConfig != null) {
         List<StepDefinition> falseSteps = new ArrayList();

         for(Map<String, Object> stepConfig : falseStepsConfig) {
            falseSteps.add(this.parseStep(stepConfig));
         }

         result.put("falseSteps", falseSteps);
      }

      List<Map<String, Object>> casesConfig = (List)conditionalConfig.get("cases");
      if (casesConfig != null) {
         List<Map<String, Object>> cases = new ArrayList();

         for(Map<String, Object> caseConfig : casesConfig) {
            Map<String, Object> caseResult = new HashMap();
            caseResult.put("condition", caseConfig.get("condition"));
            List<Map<String, Object>> stepsConfig = (List)caseConfig.get("steps");
            if (stepsConfig != null) {
               List<StepDefinition> steps = new ArrayList();

               for(Map<String, Object> stepConfig : stepsConfig) {
                  steps.add(this.parseStep(stepConfig));
               }

               caseResult.put("steps", steps);
            }

            cases.add(caseResult);
         }

         result.put("cases", cases);
      }

      List<Map<String, Object>> defaultStepsConfig = (List)conditionalConfig.get("defaultSteps");
      if (defaultStepsConfig != null) {
         List<StepDefinition> defaultSteps = new ArrayList();

         for(Map<String, Object> stepConfig : defaultStepsConfig) {
            defaultSteps.add(this.parseStep(stepConfig));
         }

         result.put("defaultSteps", defaultSteps);
      }

      return result;
   }

   private StepDefinition.StepType parseStepType(String type) {
      try {
         return StepDefinition.StepType.valueOf(type.toUpperCase());
      } catch (IllegalArgumentException var3) {
         LogUtils.warn("Unknown step type: {}, defaulting to SIMPLE", new Object[]{type});
         return StepDefinition.StepType.SIMPLE;
      }
   }
}
