package com.kuma.boot.flowengine.flow;

import java.util.LinkedList;
import java.util.Objects;

public class ConditionalStep<Context, Result> extends AbstractStep<Context, Result> {
   private String name;
   private LinkedList<ConditionalStepBuilder.Tuple<Context, Result>> conditionMap = new LinkedList();
   private Step<Context, Result> otherStep = null;

   public ConditionalStep(String name, String flowName, LinkedList<ConditionalStepBuilder.Tuple<Context, Result>> conditionMap, Step<Context, Result> otherStep) {
      super(name, flowName);
      Objects.requireNonNull(conditionMap);
      this.conditionMap = conditionMap;
      this.otherStep = otherStep;
   }

   public boolean hasAction(Context context) {
      return true;
   }

   public FlowResultHolder<Result> execute(Context context) {
      for(ConditionalStepBuilder.Tuple<Context, Result> tuple : this.conditionMap) {
         if (tuple.getPredicate().test(context)) {
            return tuple.getSubFlow().execute(context);
         }
      }

      if (this.otherStep != null) {
         return this.otherStep.execute(context);
      } else {
         return FlowResultHolder.empty();
      }
   }
}
