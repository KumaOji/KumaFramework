package com.kuma.boot.flowengine.flow;

import java.util.function.Function;
import java.util.function.Predicate;

public class EndStep<Context, Result> extends PredicatableStep<Context, Result> {
   private Function<Context, Result> mapper;

   public EndStep(String name, String flowName, Function<Context, Result> mapper, Predicate<Context> predicate) {
      super(name, flowName, predicate);
      this.mapper = mapper;
   }

   public FlowResultHolder<Result> execute(Context context) {
      Result result = (Result)this.mapper.apply(context);
      return FlowResultHolder.<Result>of(result);
   }

   public String getName() {
      return super.getName() == null ? "ending" : super.getName();
   }
}
