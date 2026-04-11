package com.kuma.boot.flowengine.simpleflow.api;

import com.kuma.boot.flowengine.simpleflow.api.model.StepDefinition;
import com.kuma.boot.flowengine.simpleflow.api.model.StepResult;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public interface FlowContext {
   String getExecutionId();

   String getFlowId();

   String getFlowName();

   LocalDateTime getStartTime();

   LocalDateTime getEndTime();

   String getStatus();

   void setStatus(String status);

   <T> Optional<T> get(String key);

   <T> T get(String key, T defaultValue);

   void set(String key, Object value);

   <T> Optional<T> remove(String key);

   boolean contains(String key);

   Map<String, Object> getAll();

   void setAll(Map<String, Object> variables);

   void clear();

   Optional<StepResult> getStepResult(String stepId);

   void setStepResult(String stepId, StepResult result);

   Map<String, StepResult> getAllStepResults();

   Optional<String> getCurrentStepId();

   void setCurrentStepId(String stepId);

   Optional<Exception> getError();

   void setError(Exception error);

   boolean hasError();

   int getRetryCount();

   void incrementRetryCount();

   void resetRetryCount();

   boolean isCancelled();

   void cancel();

   boolean isPaused();

   void pause();

   void resume();

   FlowContext createChildContext(String stepId);

   Optional<FlowContext> getParentContext();

   FlowContext copy();

   default Optional<StepDefinition> getCurrentStepDefinition() {
      return this.<StepDefinition>get("currentStepDefinition");
   }

   default void setCurrentStepDefinition(StepDefinition stepDefinition) {
      this.set("currentStepDefinition", stepDefinition);
   }

   default Map<String, Object> getVariables() {
      return this.getAll();
   }

   default <T> Optional<T> getVariable(String key) {
      return this.<T>get(key);
   }
}
