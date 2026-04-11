package com.kuma.boot.flowengine.simpleflow.api;

import com.kuma.boot.flowengine.simpleflow.api.model.StepResult;

@FunctionalInterface
public interface StepExecutor {
   StepResult execute(FlowContext context) throws Exception;

   default String getName() {
      return this.getClass().getSimpleName();
   }

   default String getDescription() {
      return "Step executor: " + this.getName();
   }

   default boolean supportsRetry() {
      return true;
   }

   default boolean supportsParallel() {
      return true;
   }

   default long getEstimatedExecutionTime() {
      return -1L;
   }

   default void prepare(FlowContext context) throws Exception {
   }

   default void cleanup(FlowContext context, StepResult result) {
   }

   default boolean validate(FlowContext context) {
      return true;
   }

   static StepExecutor of(StepExecutor executor) {
      return executor;
   }

   static StepExecutor of(final String name, final StepExecutor executor) {
      return new StepExecutor() {
         public StepResult execute(FlowContext context) throws Exception {
            return executor.execute(context);
         }

         public String getName() {
            return name;
         }
      };
   }

   static StepExecutor of(final String name, final String description, final StepExecutor executor) {
      return new StepExecutor() {
         public StepResult execute(FlowContext context) throws Exception {
            return executor.execute(context);
         }

         public String getName() {
            return name;
         }

         public String getDescription() {
            return description;
         }
      };
   }
}
