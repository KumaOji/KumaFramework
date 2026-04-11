package com.kuma.boot.flowengine.simpleflow.api.exception;

public class FlowException extends RuntimeException {
   private final String flowId;
   private final String executionId;
   private final String errorCode;

   public FlowException(String message) {
      super(message);
      this.flowId = null;
      this.executionId = null;
      this.errorCode = null;
   }

   public FlowException(String message, Throwable cause) {
      super(message, cause);
      this.flowId = null;
      this.executionId = null;
      this.errorCode = null;
   }

   public FlowException(String message, String flowId) {
      super(message);
      this.flowId = flowId;
      this.executionId = null;
      this.errorCode = null;
   }

   public FlowException(String message, String flowId, Throwable cause) {
      super(message, cause);
      this.flowId = flowId;
      this.executionId = null;
      this.errorCode = null;
   }

   public FlowException(String message, String flowId, String executionId) {
      super(message);
      this.flowId = flowId;
      this.executionId = executionId;
      this.errorCode = null;
   }

   public FlowException(String message, String flowId, String executionId, Throwable cause) {
      super(message, cause);
      this.flowId = flowId;
      this.executionId = executionId;
      this.errorCode = null;
   }

   public FlowException(String message, String flowId, String executionId, String errorCode) {
      super(message);
      this.flowId = flowId;
      this.executionId = executionId;
      this.errorCode = errorCode;
   }

   public FlowException(String message, String flowId, String executionId, String errorCode, Throwable cause) {
      super(message, cause);
      this.flowId = flowId;
      this.executionId = executionId;
      this.errorCode = errorCode;
   }

   public String getFlowId() {
      return this.flowId;
   }

   public String getExecutionId() {
      return this.executionId;
   }

   public String getErrorCode() {
      return this.errorCode;
   }

   public String getDetailedMessage() {
      StringBuilder sb = new StringBuilder();
      sb.append(this.getMessage());
      if (this.flowId != null) {
         sb.append(" [FlowId: ").append(this.flowId).append("]");
      }

      if (this.executionId != null) {
         sb.append(" [ExecutionId: ").append(this.executionId).append("]");
      }

      if (this.errorCode != null) {
         sb.append(" [ErrorCode: ").append(this.errorCode).append("]");
      }

      return sb.toString();
   }

   public String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + ": " + this.getDetailedMessage();
   }
}
