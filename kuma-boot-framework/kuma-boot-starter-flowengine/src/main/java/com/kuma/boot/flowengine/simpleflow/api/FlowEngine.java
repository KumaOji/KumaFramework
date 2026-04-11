package com.kuma.boot.flowengine.simpleflow.api;

import com.kuma.boot.flowengine.simpleflow.api.model.FlowDefinition;
import com.kuma.boot.flowengine.simpleflow.api.model.FlowResult;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface FlowEngine {
   FlowResult execute(FlowDefinition flowDefinition, Map<String, Object> context);

   CompletableFuture<FlowResult> executeAsync(FlowDefinition flowDefinition, Map<String, Object> context);

   FlowResult execute(String flowId, Map<String, Object> context);

   CompletableFuture<FlowResult> executeAsync(String flowId, Map<String, Object> context);

   boolean stopExecution(String executionId);

   boolean pauseExecution(String executionId);

   boolean resumeExecution(String executionId);

   String getExecutionStatus(String executionId);

   String registerFlow(FlowDefinition flowDefinition);

   boolean unregisterFlow(String flowId);

   FlowDefinition getFlowDefinition(String flowId);

   boolean isHealthy();

   void shutdown();
}
