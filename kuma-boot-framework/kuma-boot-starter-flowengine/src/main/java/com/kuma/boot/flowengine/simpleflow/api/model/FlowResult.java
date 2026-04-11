package com.kuma.boot.flowengine.simpleflow.api.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class FlowResult {
   private String executionId;
   private String flowId;
   private String flowName;
   private Status status;
   private LocalDateTime startTime;
   private LocalDateTime endTime;
   private long durationMs;
   private Map<String, StepResult> stepResults;
   private Map<String, Object> outputData;
   private Exception error;
   private String errorMessage;
   private List<String> warnings;
   private Map<String, Object> metadata;
   private int totalSteps;
   private int successfulSteps;
   private int failedSteps;
   private int skippedSteps;

   public FlowResult() {
   }

   public String getExecutionId() {
      return this.executionId;
   }

   public void setExecutionId(String executionId) {
      this.executionId = executionId;
   }

   public String getFlowId() {
      return this.flowId;
   }

   public void setFlowId(String flowId) {
      this.flowId = flowId;
   }

   public String getFlowName() {
      return this.flowName;
   }

   public void setFlowName(String flowName) {
      this.flowName = flowName;
   }

   public Status getStatus() {
      return this.status;
   }

   public void setStatus(Status status) {
      this.status = status;
   }

   public LocalDateTime getStartTime() {
      return this.startTime;
   }

   public void setStartTime(LocalDateTime startTime) {
      this.startTime = startTime;
   }

   public LocalDateTime getEndTime() {
      return this.endTime;
   }

   public void setEndTime(LocalDateTime endTime) {
      this.endTime = endTime;
   }

   public long getDurationMs() {
      return this.durationMs;
   }

   public void setDurationMs(long durationMs) {
      this.durationMs = durationMs;
   }

   public Map<String, StepResult> getStepResults() {
      return this.stepResults;
   }

   public void setStepResults(Map<String, StepResult> stepResults) {
      this.stepResults = stepResults;
   }

   public Map<String, Object> getOutputData() {
      return this.outputData;
   }

   public void setOutputData(Map<String, Object> outputData) {
      this.outputData = outputData;
   }

   public Exception getError() {
      return this.error;
   }

   public void setError(Exception error) {
      this.error = error;
   }

   public String getErrorMessage() {
      return this.errorMessage;
   }

   public void setErrorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
   }

   public List<String> getWarnings() {
      return this.warnings;
   }

   public void setWarnings(List<String> warnings) {
      this.warnings = warnings;
   }

   public Map<String, Object> getMetadata() {
      return this.metadata;
   }

   public void setMetadata(Map<String, Object> metadata) {
      this.metadata = metadata;
   }

   public int getTotalSteps() {
      return this.totalSteps;
   }

   public void setTotalSteps(int totalSteps) {
      this.totalSteps = totalSteps;
   }

   public int getSuccessfulSteps() {
      return this.successfulSteps;
   }

   public void setSuccessfulSteps(int successfulSteps) {
      this.successfulSteps = successfulSteps;
   }

   public int getFailedSteps() {
      return this.failedSteps;
   }

   public void setFailedSteps(int failedSteps) {
      this.failedSteps = failedSteps;
   }

   public int getSkippedSteps() {
      return this.skippedSteps;
   }

   public void setSkippedSteps(int skippedSteps) {
      this.skippedSteps = skippedSteps;
   }

   public Optional<StepResult> getStepResult(String stepId) {
      return Optional.ofNullable((StepResult)this.stepResults.get(stepId));
   }

   public <T> Optional<T> getOutputData(String key) {
      return Optional.ofNullable(this.outputData.get(key));
   }

   public <T> T getOutputData(String key, T defaultValue) {
      return (T)this.outputData.getOrDefault(key, defaultValue);
   }

   public <T> Optional<T> getMetadata(String key) {
      return Optional.ofNullable(this.metadata.get(key));
   }

   public boolean isSuccess() {
      return this.status == FlowResult.Status.SUCCESS;
   }

   public boolean isFailed() {
      return this.status == FlowResult.Status.FAILED;
   }

   public boolean isCancelled() {
      return this.status == FlowResult.Status.CANCELLED;
   }

   public boolean isTimeout() {
      return this.status == FlowResult.Status.TIMEOUT;
   }

   public boolean isPartial() {
      return this.status == FlowResult.Status.PARTIAL;
   }

   public boolean hasError() {
      return this.error != null || this.errorMessage != null;
   }

   public boolean hasWarnings() {
      return !this.warnings.isEmpty();
   }

   public double getSuccessRate() {
      return this.totalSteps == 0 ? (double)0.0F : (double)this.successfulSteps / (double)this.totalSteps;
   }

   public List<StepResult> getFailedStepResults() {
      return (List)this.stepResults.values().stream().filter((result) -> result.getStatus() == StepResult.Status.FAILED).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
   }

   public List<StepResult> getSuccessfulStepResults() {
      return (List)this.stepResults.values().stream().filter((result) -> result.getStatus() == StepResult.Status.SUCCESS).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         FlowResult that = (FlowResult)o;
         return Objects.equals(this.executionId, that.executionId);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.executionId});
   }

   public String toString() {
      String var10000 = this.executionId;
      return "FlowResult{executionId='" + var10000 + "', flowId='" + this.flowId + "', status=" + String.valueOf(this.status) + ", durationMs=" + this.durationMs + ", totalSteps=" + this.totalSteps + ", successfulSteps=" + this.successfulSteps + ", failedSteps=" + this.failedSteps + "}";
   }

   public static FlowResultBuilder builder() {
      return new FlowResultBuilder();
   }

   public static enum Status {
      SUCCESS,
      FAILED,
      CANCELLED,
      TIMEOUT,
      PARTIAL;

      private Status() {
      }

      // $FF: synthetic method
      private static Status[] $values() {
         return new Status[]{SUCCESS, FAILED, CANCELLED, TIMEOUT, PARTIAL};
      }
   }

   public static final class FlowResultBuilder {
      private String executionId;
      private String flowId;
      private String flowName;
      private Status status;
      private LocalDateTime startTime;
      private LocalDateTime endTime;
      private long durationMs;
      private Map<String, StepResult> stepResults;
      private Map<String, Object> outputData;
      private Exception error;
      private String errorMessage;
      private List<String> warnings;
      private Map<String, Object> metadata;
      private int totalSteps;
      private int successfulSteps;
      private int failedSteps;
      private int skippedSteps;

      private FlowResultBuilder() {
      }

      public FlowResultBuilder executionId(String executionId) {
         this.executionId = executionId;
         return this;
      }

      public FlowResultBuilder flowId(String flowId) {
         this.flowId = flowId;
         return this;
      }

      public FlowResultBuilder flowName(String flowName) {
         this.flowName = flowName;
         return this;
      }

      public FlowResultBuilder status(Status status) {
         this.status = status;
         return this;
      }

      public FlowResultBuilder startTime(LocalDateTime startTime) {
         this.startTime = startTime;
         return this;
      }

      public FlowResultBuilder endTime(LocalDateTime endTime) {
         this.endTime = endTime;
         return this;
      }

      public FlowResultBuilder durationMs(long durationMs) {
         this.durationMs = durationMs;
         return this;
      }

      public FlowResultBuilder stepResults(Map<String, StepResult> stepResults) {
         this.stepResults = stepResults;
         return this;
      }

      public FlowResultBuilder outputData(Map<String, Object> outputData) {
         this.outputData = outputData;
         return this;
      }

      public FlowResultBuilder error(Exception error) {
         this.error = error;
         return this;
      }

      public FlowResultBuilder errorMessage(String errorMessage) {
         this.errorMessage = errorMessage;
         return this;
      }

      public FlowResultBuilder warnings(List<String> warnings) {
         this.warnings = warnings;
         return this;
      }

      public FlowResultBuilder metadata(Map<String, Object> metadata) {
         this.metadata = metadata;
         return this;
      }

      public FlowResultBuilder totalSteps(int totalSteps) {
         this.totalSteps = totalSteps;
         return this;
      }

      public FlowResultBuilder successfulSteps(int successfulSteps) {
         this.successfulSteps = successfulSteps;
         return this;
      }

      public FlowResultBuilder failedSteps(int failedSteps) {
         this.failedSteps = failedSteps;
         return this;
      }

      public FlowResultBuilder skippedSteps(int skippedSteps) {
         this.skippedSteps = skippedSteps;
         return this;
      }

      public FlowResult build() {
         FlowResult flowResult = new FlowResult();
         flowResult.setExecutionId(this.executionId);
         flowResult.setFlowId(this.flowId);
         flowResult.setFlowName(this.flowName);
         flowResult.setStatus(this.status);
         flowResult.setStartTime(this.startTime);
         flowResult.setEndTime(this.endTime);
         flowResult.setDurationMs(this.durationMs);
         flowResult.setStepResults(this.stepResults);
         flowResult.setOutputData(this.outputData);
         flowResult.setError(this.error);
         flowResult.setErrorMessage(this.errorMessage);
         flowResult.setWarnings(this.warnings);
         flowResult.setMetadata(this.metadata);
         flowResult.setTotalSteps(this.totalSteps);
         flowResult.setSuccessfulSteps(this.successfulSteps);
         flowResult.setFailedSteps(this.failedSteps);
         flowResult.setSkippedSteps(this.skippedSteps);
         return flowResult;
      }
   }
}
