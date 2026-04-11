package com.kuma.boot.flowengine.simpleflow.api.model;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 流程执行结果模型
 *
 * 包含流程执行的完整结果信息
 *
 * @author Simple Flow Team
 * @since 1.0.0
 */
public class FlowResult {

   /**
    * 执行状态枚举
    */
   public enum Status {
      SUCCESS,    // 成功
      FAILED,     // 失败
      CANCELLED,  // 取消
      TIMEOUT,    // 超时
      PARTIAL     // 部分成功
   }

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

   public String getExecutionId() {
      return executionId;
   }

   public void setExecutionId(String executionId) {
      this.executionId = executionId;
   }

   public String getFlowId() {
      return flowId;
   }

   public void setFlowId(String flowId) {
      this.flowId = flowId;
   }

   public String getFlowName() {
      return flowName;
   }

   public void setFlowName(String flowName) {
      this.flowName = flowName;
   }

   public Status getStatus() {
      return status;
   }

   public void setStatus(Status status) {
      this.status = status;
   }

   public LocalDateTime getStartTime() {
      return startTime;
   }

   public void setStartTime(LocalDateTime startTime) {
      this.startTime = startTime;
   }

   public LocalDateTime getEndTime() {
      return endTime;
   }

   public void setEndTime(LocalDateTime endTime) {
      this.endTime = endTime;
   }

   public long getDurationMs() {
      return durationMs;
   }

   public void setDurationMs(long durationMs) {
      this.durationMs = durationMs;
   }

   public Map<String, StepResult> getStepResults() {
      return stepResults;
   }

   public void setStepResults(
           Map<String, StepResult> stepResults) {
      this.stepResults = stepResults;
   }

   public Map<String, Object> getOutputData() {
      return outputData;
   }

   public void setOutputData(Map<String, Object> outputData) {
      this.outputData = outputData;
   }

   public Exception getError() {
      return error;
   }

   public void setError(Exception error) {
      this.error = error;
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   public void setErrorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
   }

   public List<String> getWarnings() {
      return warnings;
   }

   public void setWarnings(List<String> warnings) {
      this.warnings = warnings;
   }

   public Map<String, Object> getMetadata() {
      return metadata;
   }

   public void setMetadata(Map<String, Object> metadata) {
      this.metadata = metadata;
   }

   public int getTotalSteps() {
      return totalSteps;
   }

   public void setTotalSteps(int totalSteps) {
      this.totalSteps = totalSteps;
   }

   public int getSuccessfulSteps() {
      return successfulSteps;
   }

   public void setSuccessfulSteps(int successfulSteps) {
      this.successfulSteps = successfulSteps;
   }

   public int getFailedSteps() {
      return failedSteps;
   }

   public void setFailedSteps(int failedSteps) {
      this.failedSteps = failedSteps;
   }

   public int getSkippedSteps() {
      return skippedSteps;
   }

   public void setSkippedSteps(int skippedSteps) {
      this.skippedSteps = skippedSteps;
   }

   /**
    * 获取指定步骤的结果
    *
    * @param stepId 步骤ID
    * @return 步骤结果，如果不存在则返回Optional.empty()
    */
   public Optional<StepResult> getStepResult(String stepId) {
      return Optional.ofNullable(stepResults.get(stepId));
   }

   /**
    * 获取指定输出数据
    *
    * @param <T> 返回值类型
    * @param key 数据键
    * @return 输出数据，如果不存在则返回Optional.empty()
    */
   @SuppressWarnings("unchecked")
   public <T> Optional<T> getOutputData(String key) {
      return Optional.ofNullable((T) outputData.get(key));
   }

   /**
    * 获取指定输出数据，如果不存在则返回默认值
    *
    * @param <T> 返回值类型
    * @param key 数据键
    * @param defaultValue 默认值
    * @return 输出数据或默认值
    */
   @SuppressWarnings("unchecked")
   public <T> T getOutputData(String key, T defaultValue) {
      return (T) outputData.getOrDefault(key, defaultValue);
   }

   /**
    * 获取指定元数据
    *
    * @param <T> 返回值类型
    * @param key 元数据键
    * @return 元数据，如果不存在则返回Optional.empty()
    */
   @SuppressWarnings("unchecked")
   public <T> Optional<T> getMetadata(String key) {
      return Optional.ofNullable((T) metadata.get(key));
   }

   /**
    * 检查是否成功
    *
    * @return 是否成功
    */
   public boolean isSuccess() {
      return status == Status.SUCCESS;
   }

   /**
    * 检查是否失败
    *
    * @return 是否失败
    */
   public boolean isFailed() {
      return status == Status.FAILED;
   }

   /**
    * 检查是否被取消
    *
    * @return 是否被取消
    */
   public boolean isCancelled() {
      return status == Status.CANCELLED;
   }

   /**
    * 检查是否超时
    */
   public boolean isTimeout() {
      return status == Status.TIMEOUT;
   }

   /**
    * 检查是否部分成功
    */
   public boolean isPartial() {
      return status == Status.PARTIAL;
   }

   /**
    * 检查是否有错误
    */
   public boolean hasError() {
      return error != null || errorMessage != null;
   }

   /**
    * 检查是否有警告
    */
   public boolean hasWarnings() {
      return !warnings.isEmpty();
   }

   /**
    * 获取成功率
    */
   public double getSuccessRate() {
      if (totalSteps == 0) {
         return 0.0;
      }
      return (double) successfulSteps / totalSteps;
   }

   /**
    * 获取失败的步骤结果
    */
   public List<StepResult> getFailedStepResults() {
      return stepResults.values().stream()
              .filter(result -> result.getStatus() == StepResult.Status.FAILED)
              .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
   }

   /**
    * 获取成功的步骤结果
    */
   public List<StepResult> getSuccessfulStepResults() {
      return stepResults.values().stream()
              .filter(result -> result.getStatus() == StepResult.Status.SUCCESS)
              .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      FlowResult that = (FlowResult) o;
      return Objects.equals(executionId, that.executionId);
   }

   @Override
   public int hashCode() {
      return Objects.hash(executionId);
   }

   @Override
   public String toString() {
      return "FlowResult{" +
              "executionId='" + executionId + '\'' +
              ", flowId='" + flowId + '\'' +
              ", status=" + status +
              ", durationMs=" + durationMs +
              ", totalSteps=" + totalSteps +
              ", successfulSteps=" + successfulSteps +
              ", failedSteps=" + failedSteps +
              '}';
   }


   public static FlowResultBuilder builder() {
      return new FlowResultBuilder();
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
         flowResult.setExecutionId(executionId);
         flowResult.setFlowId(flowId);
         flowResult.setFlowName(flowName);
         flowResult.setStatus(status);
         flowResult.setStartTime(startTime);
         flowResult.setEndTime(endTime);
         flowResult.setDurationMs(durationMs);
         flowResult.setStepResults(stepResults);
         flowResult.setOutputData(outputData);
         flowResult.setError(error);
         flowResult.setErrorMessage(errorMessage);
         flowResult.setWarnings(warnings);
         flowResult.setMetadata(metadata);
         flowResult.setTotalSteps(totalSteps);
         flowResult.setSuccessfulSteps(successfulSteps);
         flowResult.setFailedSteps(failedSteps);
         flowResult.setSkippedSteps(skippedSteps);
         return flowResult;
      }
   }
}
