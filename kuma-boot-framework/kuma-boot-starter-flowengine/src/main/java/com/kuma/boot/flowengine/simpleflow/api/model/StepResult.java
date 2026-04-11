package com.kuma.boot.flowengine.simpleflow.api.model;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 步骤执行结果模型
 *
 * 包含单个步骤执行的完整结果信息
 *
 * @author Simple Flow Team
 * @since 1.0.0
 */
public class StepResult {

   /**
    * 执行状态枚举
    */
   public enum Status {
      SUCCESS,    // 成功
      FAILED,     // 失败
      SKIPPED,    // 跳过
      TIMEOUT,    // 超时
      CANCELLED,  // 取消
      RETRY       // 重试中
   }

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

   public StepResult(
           String stepId,
           String stepName,
           Status status,
           LocalDateTime startTime,
           LocalDateTime endTime,
           long durationMs,
           Map<String, Object> outputData,
           Exception error,
           String errorMessage,
           List<String> logs,
           Map<String, Object> metadata,
           int retryCount,
           String executorName,
           boolean skipped) {
      this.stepId = Objects.requireNonNull(stepId, "Step ID cannot be null");
      this.stepName = stepName;
      this.status = Objects.requireNonNull(status, "Status cannot be null");
      this.startTime = startTime;
      this.endTime = endTime;
      this.durationMs = durationMs;
      this.outputData = outputData != null ? Collections.unmodifiableMap(new HashMap<>(outputData)) : Collections.emptyMap();
      this.error = error;
      this.errorMessage = errorMessage;
      this.logs = logs != null ? Collections.unmodifiableList(new ArrayList<>(logs)) : Collections.emptyList();
      this.metadata = metadata != null ? Collections.unmodifiableMap(new HashMap<>(metadata)) : Collections.emptyMap();
      this.retryCount = Math.max(0, retryCount);
      this.executorName = executorName;
      this.skipped = skipped;
   }

   public boolean isSkipped() {
      return skipped;
   }

   public void setSkipped(boolean skipped) {
      this.skipped = skipped;
   }

   public String getExecutorName() {
      return executorName;
   }

   public void setExecutorName(String executorName) {
      this.executorName = executorName;
   }

   public int getRetryCount() {
      return retryCount;
   }

   public void setRetryCount(int retryCount) {
      this.retryCount = retryCount;
   }

   public Map<String, Object> getMetadata() {
      return metadata;
   }

   public void setMetadata(Map<String, Object> metadata) {
      this.metadata = metadata;
   }

   public List<String> getLogs() {
      return logs;
   }

   public void setLogs(List<String> logs) {
      this.logs = logs;
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   public void setErrorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
   }

   public Exception getError() {
      return error;
   }

   public void setError(Exception error) {
      this.error = error;
   }

   public Map<String, Object> getOutputData() {
      return outputData;
   }

   public void setOutputData(Map<String, Object> outputData) {
      this.outputData = outputData;
   }

   public long getDurationMs() {
      return durationMs;
   }

   public void setDurationMs(long durationMs) {
      this.durationMs = durationMs;
   }

   public LocalDateTime getEndTime() {
      return endTime;
   }

   public void setEndTime(LocalDateTime endTime) {
      this.endTime = endTime;
   }

   public LocalDateTime getStartTime() {
      return startTime;
   }

   public void setStartTime(LocalDateTime startTime) {
      this.startTime = startTime;
   }

   public Status getStatus() {
      return status;
   }

   public void setStatus(Status status) {
      this.status = status;
   }

   public String getStepName() {
      return stepName;
   }

   public void setStepName(String stepName) {
      this.stepName = stepName;
   }

   public String getStepId() {
      return stepId;
   }

   public void setStepId(String stepId) {
      this.stepId = stepId;
   }

   /**
    * 获取指定输出数据
    */
   @SuppressWarnings("unchecked")
   public <T> Optional<T> getOutputData(String key) {
      return Optional.ofNullable((T) outputData.get(key));
   }

   /**
    * 获取指定输出数据，如果不存在则返回默认值
    */
   @SuppressWarnings("unchecked")
   public <T> T getOutputData(String key, T defaultValue) {
      return (T) outputData.getOrDefault(key, defaultValue);
   }

   /**
    * 获取指定元数据
    */
   @SuppressWarnings("unchecked")
   public <T> Optional<T> getMetadata(String key) {
      return Optional.ofNullable((T) metadata.get(key));
   }

   /**
    * 检查是否成功
    */
   public boolean isSuccess() {
      return status == Status.SUCCESS;
   }

   /**
    * 检查是否失败
    */
   public boolean isFailed() {
      return status == Status.FAILED;
   }

   /**
    * 检查是否超时
    */
   public boolean isTimeout() {
      return status == Status.TIMEOUT;
   }

   /**
    * 检查是否被取消
    */
   public boolean isCancelled() {
      return status == Status.CANCELLED;
   }

   /**
    * 检查是否在重试中
    */
   public boolean isRetrying() {
      return status == Status.RETRY;
   }

   /**
    * 检查是否有错误
    */
   public boolean hasError() {
      return error != null || errorMessage != null;
   }

   /**
    * 检查是否有日志
    */
   public boolean hasLogs() {
      return !logs.isEmpty();
   }

   /**
    * 检查是否有输出数据
    */
   public boolean hasOutputData() {
      return !outputData.isEmpty();
   }

   /**
    * 检查是否进行了重试
    */
   public boolean hasRetried() {
      return retryCount > 0;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      StepResult that = (StepResult) o;
      return Objects.equals(stepId, that.stepId) && Objects.equals(startTime, that.startTime);
   }

   @Override
   public int hashCode() {
      return Objects.hash(stepId, startTime);
   }

   @Override
   public String toString() {
      return "StepResult{" +
              "stepId='" + stepId + '\'' +
              ", stepName='" + stepName + '\'' +
              ", status=" + status +
              ", durationMs=" + durationMs +
              ", retryCount=" + retryCount +
              ", skipped=" + skipped +
              '}';
   }

   /**
    * 创建成功结果
    * @param stepId 步骤ID
    * @param stepName 步骤名称
    */
   public static StepResult success(String stepId, String stepName) {
      LocalDateTime now = LocalDateTime.now();
      return new StepResult(stepId, stepName, Status.SUCCESS, now, now, 0,
              Collections.emptyMap(), null, null, Collections.emptyList(),
              Collections.emptyMap(), 0, null, false);
   }

   /**
    * 创建成功结果（带输出数据）
    * @param stepId 步骤ID
    * @param stepName 步骤名称
    * @param outputData 输出数据
    */
   public static StepResult success(String stepId, String stepName, Map<String, Object> outputData) {
      LocalDateTime now = LocalDateTime.now();
      return new StepResult(stepId, stepName, Status.SUCCESS, now, now, 0,
              outputData, null, null, Collections.emptyList(),
              Collections.emptyMap(), 0, null, false);
   }

   /**
    * 创建失败结果
    * @param stepId 步骤ID
    * @param stepName 步骤名称
    * @param error 异常对象
    */
   public static StepResult failure(String stepId, String stepName, Exception error) {
      LocalDateTime now = LocalDateTime.now();
      return new StepResult(stepId, stepName, Status.FAILED, now, now, 0,
              Collections.emptyMap(), error, null, Collections.emptyList(),
              Collections.emptyMap(), 0, null, false);
   }

   /**
    * 创建失败结果（带错误消息）
    * @param stepId 步骤ID
    * @param stepName 步骤名称
    * @param errorMessage 错误消息
    */
   public static StepResult failure(String stepId, String stepName, String errorMessage) {
      LocalDateTime now = LocalDateTime.now();
      return new StepResult(stepId, stepName, Status.FAILED, now, now, 0,
              Collections.emptyMap(), null, errorMessage, Collections.emptyList(),
              Collections.emptyMap(), 0, null, false);
   }

   /**
    * 创建跳过结果
    * @param stepId 步骤ID
    * @param stepName 步骤名称
    * @param reason 跳过原因
    */
   public static StepResult skipped(String stepId, String stepName, String reason) {
      Map<String, Object> metadata = new HashMap<>();
      metadata.put("skipReason", reason);
      return new StepResult(stepId, stepName, Status.SKIPPED, null, null, 0,
              Collections.emptyMap(), null, null, Collections.emptyList(),
              metadata, 0, null, true);
   }

   public static StepResultBuilder builder() {
      return new StepResultBuilder();
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
         return new StepResult(stepId, stepName, status, startTime, endTime, durationMs, outputData, error,
                 errorMessage, logs, metadata, retryCount, executorName, skipped);
      }
   }
}
