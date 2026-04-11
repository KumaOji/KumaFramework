package com.kuma.boot.flowengine.simpleflow.api.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class StepResult {
   private String stepId;
   private String stepName;
   private Status status;
   private LocalDateTime startTime;
   private LocalDateTime endTime;
   private long durationMs;
   private Map<String, Object> outputData;
   private Exception error;
   private String errorMessage;
   private List<String> logs;
   private Map<String, Object> metadata;
   private int retryCount;
   private String executorName;
   private boolean skipped;

   public StepResult(String stepId, String stepName, Status status, LocalDateTime startTime, LocalDateTime endTime, long durationMs, Map<String, Object> outputData, Exception error, String errorMessage, List<String> logs, Map<String, Object> metadata, int retryCount, String executorName, boolean skipped) {
      this.stepId = (String)Objects.requireNonNull(stepId, "Step ID cannot be null");
      this.stepName = stepName;
      this.status = (Status)Objects.requireNonNull(status, "Status cannot be null");
      this.startTime = startTime;
      this.endTime = endTime;
      this.durationMs = durationMs;
      this.outputData = outputData != null ? Collections.unmodifiableMap(new HashMap(outputData)) : Collections.emptyMap();
      this.error = error;
      this.errorMessage = errorMessage;
      this.logs = logs != null ? Collections.unmodifiableList(new ArrayList(logs)) : Collections.emptyList();
      this.metadata = metadata != null ? Collections.unmodifiableMap(new HashMap(metadata)) : Collections.emptyMap();
      this.retryCount = Math.max(0, retryCount);
      this.executorName = executorName;
      this.skipped = skipped;
   }

   public boolean isSkipped() {
      return this.skipped;
   }

   public void setSkipped(boolean skipped) {
      this.skipped = skipped;
   }

   public String getExecutorName() {
      return this.executorName;
   }

   public void setExecutorName(String executorName) {
      this.executorName = executorName;
   }

   public int getRetryCount() {
      return this.retryCount;
   }

   public void setRetryCount(int retryCount) {
      this.retryCount = retryCount;
   }

   public Map<String, Object> getMetadata() {
      return this.metadata;
   }

   public void setMetadata(Map<String, Object> metadata) {
      this.metadata = metadata;
   }

   public List<String> getLogs() {
      return this.logs;
   }

   public void setLogs(List<String> logs) {
      this.logs = logs;
   }

   public String getErrorMessage() {
      return this.errorMessage;
   }

   public void setErrorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
   }

   public Exception getError() {
      return this.error;
   }

   public void setError(Exception error) {
      this.error = error;
   }

   public Map<String, Object> getOutputData() {
      return this.outputData;
   }

   public void setOutputData(Map<String, Object> outputData) {
      this.outputData = outputData;
   }

   public long getDurationMs() {
      return this.durationMs;
   }

   public void setDurationMs(long durationMs) {
      this.durationMs = durationMs;
   }

   public LocalDateTime getEndTime() {
      return this.endTime;
   }

   public void setEndTime(LocalDateTime endTime) {
      this.endTime = endTime;
   }

   public LocalDateTime getStartTime() {
      return this.startTime;
   }

   public void setStartTime(LocalDateTime startTime) {
      this.startTime = startTime;
   }

   public Status getStatus() {
      return this.status;
   }

   public void setStatus(Status status) {
      this.status = status;
   }

   public String getStepName() {
      return this.stepName;
   }

   public void setStepName(String stepName) {
      this.stepName = stepName;
   }

   public String getStepId() {
      return this.stepId;
   }

   public void setStepId(String stepId) {
      this.stepId = stepId;
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
      return this.status == StepResult.Status.SUCCESS;
   }

   public boolean isFailed() {
      return this.status == StepResult.Status.FAILED;
   }

   public boolean isTimeout() {
      return this.status == StepResult.Status.TIMEOUT;
   }

   public boolean isCancelled() {
      return this.status == StepResult.Status.CANCELLED;
   }

   public boolean isRetrying() {
      return this.status == StepResult.Status.RETRY;
   }

   public boolean hasError() {
      return this.error != null || this.errorMessage != null;
   }

   public boolean hasLogs() {
      return !this.logs.isEmpty();
   }

   public boolean hasOutputData() {
      return !this.outputData.isEmpty();
   }

   public boolean hasRetried() {
      return this.retryCount > 0;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         StepResult that = (StepResult)o;
         return Objects.equals(this.stepId, that.stepId) && Objects.equals(this.startTime, that.startTime);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.stepId, this.startTime});
   }

   public String toString() {
      String var10000 = this.stepId;
      return "StepResult{stepId='" + var10000 + "', stepName='" + this.stepName + "', status=" + String.valueOf(this.status) + ", durationMs=" + this.durationMs + ", retryCount=" + this.retryCount + ", skipped=" + this.skipped + "}";
   }

   public static StepResult success(String stepId, String stepName) {
      LocalDateTime now = LocalDateTime.now();
      return new StepResult(stepId, stepName, StepResult.Status.SUCCESS, now, now, 0L, Collections.emptyMap(), (Exception)null, (String)null, Collections.emptyList(), Collections.emptyMap(), 0, (String)null, false);
   }

   public static StepResult success(String stepId, String stepName, Map<String, Object> outputData) {
      LocalDateTime now = LocalDateTime.now();
      return new StepResult(stepId, stepName, StepResult.Status.SUCCESS, now, now, 0L, outputData, (Exception)null, (String)null, Collections.emptyList(), Collections.emptyMap(), 0, (String)null, false);
   }

   public static StepResult failure(String stepId, String stepName, Exception error) {
      LocalDateTime now = LocalDateTime.now();
      return new StepResult(stepId, stepName, StepResult.Status.FAILED, now, now, 0L, Collections.emptyMap(), error, (String)null, Collections.emptyList(), Collections.emptyMap(), 0, (String)null, false);
   }

   public static StepResult failure(String stepId, String stepName, String errorMessage) {
      LocalDateTime now = LocalDateTime.now();
      return new StepResult(stepId, stepName, StepResult.Status.FAILED, now, now, 0L, Collections.emptyMap(), (Exception)null, errorMessage, Collections.emptyList(), Collections.emptyMap(), 0, (String)null, false);
   }

   public static StepResult skipped(String stepId, String stepName, String reason) {
      Map<String, Object> metadata = new HashMap();
      metadata.put("skipReason", reason);
      return new StepResult(stepId, stepName, StepResult.Status.SKIPPED, (LocalDateTime)null, (LocalDateTime)null, 0L, Collections.emptyMap(), (Exception)null, (String)null, Collections.emptyList(), metadata, 0, (String)null, true);
   }

   public static StepResultBuilder builder() {
      return new StepResultBuilder();
   }

   public static enum Status {
      SUCCESS,
      FAILED,
      SKIPPED,
      TIMEOUT,
      CANCELLED,
      RETRY;

      private Status() {
      }

      // $FF: synthetic method
      private static Status[] $values() {
         return new Status[]{SUCCESS, FAILED, SKIPPED, TIMEOUT, CANCELLED, RETRY};
      }
   }

   public static final class StepResultBuilder {
      private String stepId;
      private String stepName;
      private Status status;
      private LocalDateTime startTime;
      private LocalDateTime endTime;
      private long durationMs;
      private Map<String, Object> outputData;
      private Exception error;
      private String errorMessage;
      private List<String> logs;
      private Map<String, Object> metadata;
      private int retryCount;
      private String executorName;
      private boolean skipped;

      private StepResultBuilder() {
      }

      public StepResultBuilder stepId(String stepId) {
         this.stepId = stepId;
         return this;
      }

      public StepResultBuilder stepName(String stepName) {
         this.stepName = stepName;
         return this;
      }

      public StepResultBuilder status(Status status) {
         this.status = status;
         return this;
      }

      public StepResultBuilder startTime(LocalDateTime startTime) {
         this.startTime = startTime;
         return this;
      }

      public StepResultBuilder endTime(LocalDateTime endTime) {
         this.endTime = endTime;
         return this;
      }

      public StepResultBuilder durationMs(long durationMs) {
         this.durationMs = durationMs;
         return this;
      }

      public StepResultBuilder outputData(Map<String, Object> outputData) {
         this.outputData = outputData;
         return this;
      }

      public StepResultBuilder error(Exception error) {
         this.error = error;
         return this;
      }

      public StepResultBuilder errorMessage(String errorMessage) {
         this.errorMessage = errorMessage;
         return this;
      }

      public StepResultBuilder logs(List<String> logs) {
         this.logs = logs;
         return this;
      }

      public StepResultBuilder metadata(Map<String, Object> metadata) {
         this.metadata = metadata;
         return this;
      }

      public StepResultBuilder retryCount(int retryCount) {
         this.retryCount = retryCount;
         return this;
      }

      public StepResultBuilder executorName(String executorName) {
         this.executorName = executorName;
         return this;
      }

      public StepResultBuilder skipped(boolean skipped) {
         this.skipped = skipped;
         return this;
      }

      public StepResult build() {
         return new StepResult(this.stepId, this.stepName, this.status, this.startTime, this.endTime, this.durationMs, this.outputData, this.error, this.errorMessage, this.logs, this.metadata, this.retryCount, this.executorName, this.skipped);
      }
   }
}
