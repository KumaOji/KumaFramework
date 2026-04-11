package com.kuma.boot.flowengine.flow;

public class FlowDefinitionBuilder<Context, Result> extends AbstractFlowDefinitionBuilder<Context> {
   protected ConditionalStepBuilder conditionalStepBuilder;

   public FlowDefinitionBuilder(String name, ConditionalStepBuilder<Context, Result> conditionalStepBuilder) {
      super(name);
      this.conditionalStepBuilder = conditionalStepBuilder;
   }

   public FlowDefinitionBuilder(String name) {
      this(name, (ConditionalStepBuilder)null);
   }

   public StepBuilder<Context, Result> step(String name) {
      return new StepBuilder<Context, Result>(this, name);
   }

   public FlowDefinition<Context, Result> build() {
      assert this.conditionalStepBuilder == null;

      return new FlowDefinition<Context, Result>(this.getName(), this.getSteps());
   }

   public ConditionalStepBuilder<Context, Result> close() {
      assert this.conditionalStepBuilder != null;

      this.conditionalStepBuilder.accept(this);
      return this.conditionalStepBuilder;
   }
}
