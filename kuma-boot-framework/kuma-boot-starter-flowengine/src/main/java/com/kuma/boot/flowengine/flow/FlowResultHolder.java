package com.kuma.boot.flowengine.flow;

public class FlowResultHolder<Result> {
   private Step currentStep;
   private String currentStepPath;
   private Result result;
   private boolean terminate;

   private FlowResultHolder(boolean terminate, Result result) {
      this.terminate = terminate;
      this.result = result;
   }

   public static FlowResultHolder empty() {
      return new FlowResultHolder(false, (Object)null);
   }

   public static <Result> FlowResultHolder<Result> of(Result result) {
      return new FlowResultHolder<Result>(true, result);
   }

   public Step getCurrentStep() {
      return this.currentStep;
   }

   public void setCurrentStep(Step currentStep) {
      this.currentStep = currentStep;
   }

   public String getCurrentStepPath() {
      return this.currentStepPath;
   }

   public void setCurrentStepPath(String currentStepPath) {
      this.currentStepPath = currentStepPath;
   }

   public Result getResult() {
      return this.result;
   }

   public void setResult(Result result) {
      this.result = result;
   }

   public boolean isTerminate() {
      return this.terminate;
   }

   public void setTerminate(boolean terminate) {
      this.terminate = terminate;
   }
}
