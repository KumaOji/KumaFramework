package com.kuma.boot.flowengine.flow;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class StepBuilder<Context, Result> {
   final FlowDefinitionBuilder<Context, Result> flow;
   String step;
   Predicate<Context> predicate = null;

   public StepBuilder(FlowDefinitionBuilder<Context, Result> flow, String step) {
      this.flow = flow;
      this.step = step;
   }

   public FlowDefinitionBuilder<Context, Result> invoke(Consumer<Context> consumer) {
      this.flow.addStep(new DefaultStep(this.step, this.flow.getName(), consumer, this.predicate));
      return this.flow;
   }

   public FlowDefinitionBuilder<Context, Result> end(Function<Context, Result> mapper) {
      this.flow.addStep(new EndStep(this.step, this.flow.getName(), mapper, this.predicate));
      return this.flow;
   }

   public ConditionalStepBuilder<Context, Result> conditional() {
      return new ConditionalStepBuilder<Context, Result>(this.step, this.flow);
   }

   public <SubContext, SubResult> SubFlowStepBuilder<Context, Result, SubContext, SubResult> subflow(Function<Context, SubContext> contextMapper, BiConsumer<SubResult, Context> resultConsumer) {
      return new SubFlowStepBuilder<Context, Result, SubContext, SubResult>(this.step, this.predicate, contextMapper, resultConsumer, this.flow);
   }

   public StepBuilder<Context, Result> predicate(Predicate<Context> predicate) {
      this.predicate = predicate;
      return this;
   }
}
