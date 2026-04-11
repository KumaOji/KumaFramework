package com.kuma.boot.flowengine.simpleflow.api;

import com.kuma.boot.flowengine.simpleflow.api.model.FlowDefinition;
import com.kuma.boot.flowengine.simpleflow.api.model.StepDefinition;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public interface FlowBuilder {
   FlowBuilder id(String flowId);

   FlowBuilder name(String name);

   FlowBuilder description(String description);

   FlowBuilder version(String version);

   FlowBuilder addStep(StepDefinition stepDefinition);

   FlowBuilder addStep(String stepId, String stepName, StepExecutor executor);

   FlowBuilder addConditionalStep(String stepId, String stepName, Predicate<FlowContext> condition, StepExecutor trueExecutor, StepExecutor falseExecutor);

   FlowBuilder addParallelSteps(String groupId, String groupName, StepDefinition... steps);

   FlowBuilder addLoopStep(String stepId, String stepName, Predicate<FlowContext> condition, StepExecutor executor);

   FlowBuilder addDependency(String fromStepId, String toStepId);

   FlowBuilder addConditionalDependency(String fromStepId, String toStepId, Predicate<FlowContext> condition);

   FlowBuilder onError(Function<Exception, FlowContext> errorHandler);

   FlowBuilder onComplete(Function<FlowContext, Void> callback);

   FlowBuilder properties(Map<String, Object> properties);

   FlowBuilder property(String key, Object value);

   FlowBuilder enableParallel();

   FlowBuilder timeout(long timeoutMs);

   FlowBuilder enableRetry(int maxRetries, long retryDelayMs);

   boolean validate();

   FlowDefinition build();
}
