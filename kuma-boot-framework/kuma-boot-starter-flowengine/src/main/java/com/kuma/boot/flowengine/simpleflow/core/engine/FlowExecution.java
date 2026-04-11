package com.kuma.boot.flowengine.simpleflow.core.engine;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.api.ConditionNode;
import com.kuma.boot.flowengine.simpleflow.api.ExecutableNode;
import com.kuma.boot.flowengine.simpleflow.api.FlowContext;
import com.kuma.boot.flowengine.simpleflow.api.StepExecutor;
import com.kuma.boot.flowengine.simpleflow.api.model.FlowDefinition;
import com.kuma.boot.flowengine.simpleflow.api.model.FlowResult;
import com.kuma.boot.flowengine.simpleflow.api.model.StepDefinition;
import com.kuma.boot.flowengine.simpleflow.api.model.StepResult;
import com.kuma.boot.flowengine.simpleflow.core.executor.StandaloneStepExecutorRegistry;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class FlowExecution {
   private final String executionId;
   private final FlowDefinition flowDefinition;
   private final Map<String, Object> input;
   private final AtomicReference<String> status = new AtomicReference("RUNNING");
   private final AtomicBoolean stopped = new AtomicBoolean(false);
   private final AtomicBoolean paused = new AtomicBoolean(false);
   private final LocalDateTime startTime;
   private volatile LocalDateTime endTime;

   public FlowExecution(String executionId, FlowDefinition flowDefinition, Map<String, Object> input) {
      this.executionId = executionId;
      this.flowDefinition = flowDefinition;
      this.input = input;
      this.startTime = LocalDateTime.now();
   }

   public FlowResult execute() {
      LogUtils.info("Starting execution of flow '{}' with execution ID '{}'", new Object[]{this.flowDefinition.getId(), this.executionId});

      try {
         Map<String, StepResult> stepResults = new HashMap();
         if (this.stopped.get()) {
            this.status.set("CANCELLED");
            this.endTime = LocalDateTime.now();
            return FlowResult.builder().executionId(this.executionId).flowId(this.flowDefinition.getId()).flowName(this.flowDefinition.getName()).startTime(this.startTime).status(FlowResult.Status.CANCELLED).endTime(this.endTime).stepResults(stepResults).build();
         } else {
            LogUtils.info("Flow has {} steps to execute", new Object[]{this.flowDefinition.getSteps().size()});

            for(StepDefinition step : this.flowDefinition.getSteps()) {
               LogUtils.info("Processing step: {} with type: {}", new Object[]{step.getId(), step.getType()});
               if (this.stopped.get()) {
                  this.status.set("CANCELLED");
                  break;
               }

               while(this.paused.get() && !this.stopped.get()) {
                  try {
                     Thread.sleep(100L);
                  } catch (InterruptedException var5) {
                     Thread.currentThread().interrupt();
                     this.stopped.set(true);
                     break;
                  }
               }

               if (this.stopped.get()) {
                  this.status.set("CANCELLED");
                  break;
               }

               LogUtils.info("About to call executeStep for: {}", new Object[]{step.getId()});
               StepResult stepResult = this.executeStep(step);
               LogUtils.info("executeStep returned for {}: {}", new Object[]{step.getId(), stepResult.getStatus()});
               stepResults.put(step.getId(), stepResult);
               if (stepResult.getStatus() == StepResult.Status.FAILED) {
                  this.status.set("FAILED");
                  break;
               }
            }

            if (((String)this.status.get()).equals("RUNNING")) {
               this.status.set("SUCCESS");
            }

            this.endTime = LocalDateTime.now();
            FlowResult.Status finalStatus;
            switch ((String)this.status.get()) {
               case "SUCCESS" -> finalStatus = FlowResult.Status.SUCCESS;
               case "FAILED" -> finalStatus = FlowResult.Status.FAILED;
               case "CANCELLED" -> finalStatus = FlowResult.Status.CANCELLED;
               default -> finalStatus = FlowResult.Status.FAILED;
            }

            FlowResult result = FlowResult.builder().executionId(this.executionId).flowId(this.flowDefinition.getId()).flowName(this.flowDefinition.getName()).startTime(this.startTime).status(finalStatus).endTime(this.endTime).durationMs(Duration.between(this.startTime, this.endTime).toMillis()).error(((String)this.status.get()).equals("FAILED") ? new RuntimeException("Execution failed") : null).stepResults(stepResults).build();
            LogUtils.info("Completed execution of flow '{}' with execution ID '{}', status: {}", new Object[]{this.flowDefinition.getId(), this.executionId, result.getStatus()});
            return result;
         }
      } catch (Exception e) {
         LogUtils.error("Error during execution of flow '{}' with execution ID '{}'", new Object[]{this.flowDefinition.getId(), this.executionId, e});
         this.status.set("FAILED");
         this.endTime = LocalDateTime.now();
         return FlowResult.builder().executionId(this.executionId).flowId(this.flowDefinition.getId()).flowName(this.flowDefinition.getName()).status(FlowResult.Status.FAILED).startTime(this.startTime).endTime(this.endTime).durationMs(Duration.between(this.startTime, this.endTime).toMillis()).error(e).errorMessage(e.getMessage()).stepResults(Collections.emptyMap()).build();
      }
   }

   private StepResult executeStep(StepDefinition stepDefinition) {
      LogUtils.info("Executing step '{}' in flow '{}' with type: {}", new Object[]{stepDefinition.getId(), this.flowDefinition.getId(), stepDefinition.getType()});
      LocalDateTime stepStartTime = LocalDateTime.now();

      try {
         String executorClass = stepDefinition.getExecutorClass();
         if (executorClass == null) {
            executorClass = this.determineExecutorByBeanType(stepDefinition);
         }

         LogUtils.info("Using executor class: {} for step: {}", new Object[]{executorClass, stepDefinition.getId()});
         Class<?> clazz = Class.forName(executorClass);
         StepExecutor executor = (StepExecutor)clazz.getDeclaredConstructor().newInstance();
         LogUtils.info("Created executor instance: {} for step: {}", new Object[]{executor.getClass().getSimpleName(), stepDefinition.getId()});
         FlowContext context = this.createFlowContext(stepDefinition);
         LogUtils.info("About to execute step: {} with parameters: {}", new Object[]{stepDefinition.getId(), stepDefinition.getParameters()});
         StepResult result = executor.execute(context);
         LogUtils.info("Step execution completed: {} with status: {}", new Object[]{stepDefinition.getId(), result.getStatus()});
         LocalDateTime stepEndTime = LocalDateTime.now();
         long duration = Duration.between(stepStartTime, stepEndTime).toMillis();
         return StepResult.builder().stepId(stepDefinition.getId()).stepName(stepDefinition.getName()).status(result.getStatus()).startTime(stepStartTime).endTime(stepEndTime).durationMs(duration).outputData(result.getOutputData() != null ? result.getOutputData() : Collections.emptyMap()).logs(Collections.emptyList()).metadata(Collections.emptyMap()).retryCount(0).skipped(false).build();
      } catch (Exception e) {
         LogUtils.error("Error executing step '{}' in flow '{}'", new Object[]{stepDefinition.getId(), this.flowDefinition.getId(), e});
         LocalDateTime failureTime = LocalDateTime.now();
         long duration = Duration.between(stepStartTime, failureTime).toMillis();
         return StepResult.builder().stepId(stepDefinition.getId()).stepName(stepDefinition.getName()).status(StepResult.Status.FAILED).startTime(stepStartTime).endTime(failureTime).durationMs(duration).outputData(Collections.emptyMap()).error(e).errorMessage(e.getMessage()).logs(Collections.emptyList()).metadata(Collections.emptyMap()).retryCount(0).skipped(false).build();
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
         } else if (stepDefinition.getType() == StepDefinition.StepType.SCRIPT_CONDITIONAL) {
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

   private FlowContext createFlowContext(StepDefinition stepDefinition) {
      SimpleFlowContext context = new SimpleFlowContext(stepDefinition, this.input);
      context.set("currentStepDefinition", stepDefinition);
      return context;
   }

   public boolean stop() {
      LogUtils.info("Stopping execution: {}", new Object[]{this.executionId});
      this.stopped.set(true);
      this.paused.set(false);
      return true;
   }

   public boolean pause() {
      if (this.stopped.get()) {
         return false;
      } else {
         LogUtils.info("Pausing execution: {}", new Object[]{this.executionId});
         this.paused.set(true);
         return true;
      }
   }

   public boolean resume() {
      if (this.stopped.get()) {
         return false;
      } else {
         LogUtils.info("Resuming execution: {}", new Object[]{this.executionId});
         this.paused.set(false);
         return true;
      }
   }

   public String getStatus() {
      return this.paused.get() ? "PAUSED" : (String)this.status.get();
   }

   public String getExecutionId() {
      return this.executionId;
   }

   public FlowDefinition getFlowDefinition() {
      return this.flowDefinition;
   }

   public Map<String, Object> getInput() {
      return this.input;
   }

   public LocalDateTime getStartTime() {
      return this.startTime;
   }

   public LocalDateTime getEndTime() {
      return this.endTime;
   }

   private static class SimpleFlowContext implements FlowContext {
      private final Map<String, Object> data = new ConcurrentHashMap();
      private final StepDefinition currentStep;
      private String status = "RUNNING";
      private Exception error;
      private int retryCount = 0;
      private boolean cancelled = false;
      private boolean paused = false;

      public SimpleFlowContext(StepDefinition currentStep, Map<String, Object> input) {
         this.currentStep = currentStep;
         if (input != null) {
            this.data.putAll(input);
         }

      }

      public String getExecutionId() {
         return "simple-exec";
      }

      public String getFlowId() {
         return "simple-flow";
      }

      public String getFlowName() {
         return "Simple Flow";
      }

      public LocalDateTime getStartTime() {
         return LocalDateTime.now();
      }

      public LocalDateTime getEndTime() {
         return null;
      }

      public String getStatus() {
         return this.status;
      }

      public void setStatus(String status) {
         this.status = status;
      }

      public <T> Optional<T> get(String key) {
         return Optional.ofNullable(this.data.get(key));
      }

      public <T> T get(String key, T defaultValue) {
         return (T)this.data.getOrDefault(key, defaultValue);
      }

      public void set(String key, Object value) {
         this.data.put(key, value);
      }

      public <T> Optional<T> remove(String key) {
         return Optional.ofNullable(this.data.remove(key));
      }

      public boolean contains(String key) {
         return this.data.containsKey(key);
      }

      public Map<String, Object> getAll() {
         return new HashMap(this.data);
      }

      public void setAll(Map<String, Object> variables) {
         if (variables != null) {
            this.data.putAll(variables);
         }

      }

      public void clear() {
         this.data.clear();
      }

      public Optional<StepResult> getStepResult(String stepId) {
         return Optional.empty();
      }

      public void setStepResult(String stepId, StepResult result) {
      }

      public Map<String, StepResult> getAllStepResults() {
         return new HashMap();
      }

      public Optional<String> getCurrentStepId() {
         return Optional.ofNullable(this.currentStep != null ? this.currentStep.getId() : null);
      }

      public void setCurrentStepId(String stepId) {
      }

      public Optional<Exception> getError() {
         return Optional.ofNullable(this.error);
      }

      public void setError(Exception error) {
         this.error = error;
      }

      public boolean hasError() {
         return this.error != null;
      }

      public int getRetryCount() {
         return this.retryCount;
      }

      public void incrementRetryCount() {
         ++this.retryCount;
      }

      public void resetRetryCount() {
         this.retryCount = 0;
      }

      public boolean isCancelled() {
         return this.cancelled;
      }

      public void cancel() {
         this.cancelled = true;
      }

      public boolean isPaused() {
         return this.paused;
      }

      public void pause() {
         this.paused = true;
      }

      public void resume() {
         this.paused = false;
      }

      public FlowContext createChildContext(String stepId) {
         return new SimpleFlowContext(this.currentStep, this.data);
      }

      public Optional<FlowContext> getParentContext() {
         return Optional.empty();
      }

      public FlowContext copy() {
         SimpleFlowContext copy = new SimpleFlowContext(this.currentStep, (Map)null);
         copy.data.putAll(this.data);
         return copy;
      }
   }
}
