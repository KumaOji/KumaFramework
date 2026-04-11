package com.kuma.boot.flowengine.flow;

import java.util.function.Predicate;

public abstract class PredicatableStep<Context, Result> extends AbstractStep<Context, Result> {
   private Predicate<Context> predicate;

   public Predicate<Context> getPredicate() {
      return this.predicate;
   }

   public boolean hasAction(Context context) {
      return this.getPredicate() == null || this.getPredicate().test(context);
   }

   public PredicatableStep(String name, String flowName, Predicate<Context> predicate) {
      super(name, flowName);
      this.predicate = predicate;
   }
}
