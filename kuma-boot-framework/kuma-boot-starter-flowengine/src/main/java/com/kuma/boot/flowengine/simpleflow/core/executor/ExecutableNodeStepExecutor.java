package com.kuma.boot.flowengine.simpleflow.core.executor;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.api.ExecutableNode;
import com.kuma.boot.flowengine.simpleflow.api.FlowContext;
import com.kuma.boot.flowengine.simpleflow.api.StepExecutor;
import com.kuma.boot.flowengine.simpleflow.api.model.StepDefinition;
import com.kuma.boot.flowengine.simpleflow.api.model.StepResult;
import java.util.HashMap;
import java.util.Map;

public class ExecutableNodeStepExecutor implements StepExecutor {
   public ExecutableNodeStepExecutor() {
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
               String nodeName = (String)parameters.get("node");
               if (nodeName != null && !nodeName.trim().isEmpty()) {
                  ExecutableNode nodeInstance = StandaloneStepExecutorRegistry.getInstance().getExecutableNode(nodeName);
                  if (nodeInstance == null) {
                     return StepResult.failure(currentStep.getId(), currentStep.getName(), "ExecutableNode not found: " + nodeName);
                  } else {
                     Map<String, Object> executionContext = this.prepareExecutionContext(context, parameters);
                     LogUtils.info("Executing ExecutableNode: {} for step: {}", new Object[]{nodeName, currentStep.getId()});
                     if (!nodeInstance.validate(executionContext)) {
                        return StepResult.failure(currentStep.getId(), currentStep.getName(), "Node validation failed for: " + nodeName);
                     } else {
                        nodeInstance.prepare(executionContext);
                        nodeInstance.execute(executionContext);
                        nodeInstance.cleanup(executionContext);
                        this.updateFlowContextFromExecution(context, executionContext);
                        LogUtils.info("ExecutableNode {} executed successfully for step: {}", new Object[]{nodeName, currentStep.getId()});
                        Map<String, Object> outputData = new HashMap();
                        outputData.put("result", "ExecutableNode executed successfully");
                        return StepResult.success(currentStep.getId(), currentStep.getName(), outputData);
                     }
                  }
               } else {
                  return StepResult.failure(currentStep.getId(), currentStep.getName(), "Node name is required");
               }
            }
         }
      } catch (Exception e) {
         LogUtils.error("Error executing ExecutableNode step", new Object[]{e});
         String stepId = (String)context.getCurrentStepId().orElse("unknown");
         return StepResult.failure(stepId, "unknown", "ExecutableNode execution failed: " + e.getMessage());
      }
   }

   private Map<String, Object> prepareExecutionContext(FlowContext flowContext, Map<String, Object> parameters) {
      Map<String, Object> executionContext = new HashMap();
      executionContext.putAll(flowContext.getAll());
      if (parameters != null) {
         executionContext.putAll(parameters);
      }

      executionContext.put("_executionId", flowContext.getExecutionId());
      executionContext.put("_flowId", flowContext.getFlowId());
      executionContext.put("_flowName", flowContext.getFlowName());
      executionContext.put("_currentStepId", flowContext.getCurrentStepId().orElse((Object)null));
      executionContext.put("_startTime", flowContext.getStartTime());
      return executionContext;
   }

   private void updateFlowContextFromExecution(FlowContext flowContext, Map<String, Object> executionContext) {
      for(Map.Entry<String, Object> entry : executionContext.entrySet()) {
         String key = (String)entry.getKey();
         if (!key.startsWith("_")) {
            flowContext.set(key, entry.getValue());
         }
      }

   }
}
