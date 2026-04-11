package com.kuma.boot.flowengine.flow;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class SubFlowStep<Context, Result, SubContext, SubResult> extends PredicatableStep<Context, Result> {
   private FlowDefinition<SubContext, SubResult> subFlowDefinition;
   private Function<Context, SubContext> contextMapper;
   private BiConsumer<SubResult, Context> resultConsumer;

   public SubFlowStep(String name, String flowName, Predicate<Context> predicate, FlowDefinition<SubContext, SubResult> subFlowDefinition, Function<Context, SubContext> contextMapper, BiConsumer<SubResult, Context> resultConsumer) {
      super(name, flowName, predicate);
      this.subFlowDefinition = subFlowDefinition;
      this.resultConsumer = resultConsumer;
      this.contextMapper = contextMapper;
   }

   public FlowResultHolder<Result> execute(Context context) {
      SubContext subContext = (SubContext)this.contextMapper.apply(context);
      FlowResultHolder<SubResult> subResult = this.subFlowDefinition.execute(subContext);
      this.resultConsumer.accept(subResult.getResult(), context);
      return FlowResultHolder.empty();
   }
}
