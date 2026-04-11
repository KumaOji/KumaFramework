package com.kuma.boot.flowengine.flow;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class DefaultStep<Context> extends PredicatableStep<Context, Void> {
   private Consumer<Context> consumer;

   public DefaultStep(String name, String flowName, Consumer<Context> consumer) {
      this(name, flowName, consumer, (Predicate)null);
   }

   public DefaultStep(String name, String flowName, Consumer<Context> consumer, Predicate<Context> predicate) {
      super(name, flowName, predicate);
      this.consumer = consumer;
   }

   public FlowResultHolder execute(Context context) {
      try {
         this.consumer.accept(context);
         return FlowResultHolder.empty();
      } catch (RuntimeException e) {
         throw e;
      }
   }
}
