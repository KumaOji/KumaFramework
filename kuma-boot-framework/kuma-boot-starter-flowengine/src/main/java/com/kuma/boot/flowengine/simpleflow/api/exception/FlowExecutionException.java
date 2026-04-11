package com.kuma.boot.flowengine.simpleflow.api.exception;

public class FlowExecutionException extends FlowException {
   private final String stepId;
   private final int retryCount;

   public FlowExecutionException(String message) {
      super(message);
      this.stepId = null;
      this.retryCount = 0;
   }

   public FlowExecutionException(String message, Throwable cause) {
      super(message, cause);
      this.stepId = null;
      this.retryCount = 0;
   }

   public FlowExecutionException(String message, String flowId, String executionId) {
      super(message, flowId, executionId);
      this.stepId = null;
      this.retryCount = 0;
   }

   public FlowExecutionException(String message, String flowId, String executionId, Throwable cause) {
      super(message, flowId, executionId, cause);
      this.stepId = null;
      this.retryCount = 0;
   }

   public FlowExecutionException(String message, String flowId, String executionId, String stepId) {
      super(message, flowId, executionId);
      this.stepId = stepId;
      this.retryCount = 0;
   }

   public FlowExecutionException(String message, String flowId, String executionId, String stepId, Throwable cause) {
      super(message, flowId, executionId, cause);
      this.stepId = stepId;
      this.retryCount = 0;
   }

   public FlowExecutionException(String message, String flowId, String executionId, String stepId, int retryCount) {
      super(message, flowId, executionId);
      this.stepId = stepId;
      this.retryCount = retryCount;
   }

   public FlowExecutionException(String message, String flowId, String executionId, String stepId, int retryCount, Throwable cause) {
      super(message, flowId, executionId, cause);
      this.stepId = stepId;
      this.retryCount = retryCount;
   }

   public String getStepId() {
      return this.stepId;
   }

   public int getRetryCount() {
      return this.retryCount;
   }

   public String getDetailedMessage() {
      StringBuilder sb = new StringBuilder();
      sb.append(super.getDetailedMessage());
      if (this.stepId != null) {
         sb.append(" [StepId: ").append(this.stepId).append("]");
      }

      if (this.retryCount > 0) {
         sb.append(" [RetryCount: ").append(this.retryCount).append("]");
      }

      return sb.toString();
   }
}
