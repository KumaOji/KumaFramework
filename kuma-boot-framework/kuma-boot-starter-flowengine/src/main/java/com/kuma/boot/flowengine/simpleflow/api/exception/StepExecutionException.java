package com.kuma.boot.flowengine.simpleflow.api.exception;

public class StepExecutionException extends FlowExecutionException {
   private final String stepName;
   private final String executorClass;
   private final long executionTime;

   public StepExecutionException(String message, String stepId) {
      super(message, (String)null, (String)null, (String)stepId);
      this.stepName = null;
      this.executorClass = null;
      this.executionTime = 0L;
   }

   public StepExecutionException(String message, String stepId, Throwable cause) {
      super(message, (String)null, (String)null, stepId, cause);
      this.stepName = null;
      this.executorClass = null;
      this.executionTime = 0L;
   }

   public StepExecutionException(String message, String flowId, String executionId, String stepId) {
      super(message, flowId, executionId, stepId);
      this.stepName = null;
      this.executorClass = null;
      this.executionTime = 0L;
   }

   public StepExecutionException(String message, String flowId, String executionId, String stepId, Throwable cause) {
      super(message, flowId, executionId, stepId, cause);
      this.stepName = null;
      this.executorClass = null;
      this.executionTime = 0L;
   }

   public StepExecutionException(String message, String flowId, String executionId, String stepId, String stepName) {
      super(message, flowId, executionId, stepId);
      this.stepName = stepName;
      this.executorClass = null;
      this.executionTime = 0L;
   }

   public StepExecutionException(String message, String flowId, String executionId, String stepId, String stepName, String executorClass) {
      super(message, flowId, executionId, stepId);
      this.stepName = stepName;
      this.executorClass = executorClass;
      this.executionTime = 0L;
   }

   public StepExecutionException(String message, String flowId, String executionId, String stepId, String stepName, String executorClass, long executionTime) {
      super(message, flowId, executionId, stepId);
      this.stepName = stepName;
      this.executorClass = executorClass;
      this.executionTime = executionTime;
   }

   public StepExecutionException(String message, String flowId, String executionId, String stepId, String stepName, String executorClass, long executionTime, Throwable cause) {
      super(message, flowId, executionId, stepId, cause);
      this.stepName = stepName;
      this.executorClass = executorClass;
      this.executionTime = executionTime;
   }

   public String getStepName() {
      return this.stepName;
   }

   public String getExecutorClass() {
      return this.executorClass;
   }

   public long getExecutionTime() {
      return this.executionTime;
   }

   public String getDetailedMessage() {
      StringBuilder sb = new StringBuilder();
      sb.append(super.getDetailedMessage());
      if (this.stepName != null) {
         sb.append(" [StepName: ").append(this.stepName).append("]");
      }

      if (this.executorClass != null) {
         sb.append(" [ExecutorClass: ").append(this.executorClass).append("]");
      }

      if (this.executionTime > 0L) {
         sb.append(" [ExecutionTime: ").append(this.executionTime).append("ms]");
      }

      return sb.toString();
   }
}
