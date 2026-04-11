package com.kuma.boot.flowengine.flow;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class CloseStep<Context> extends PredicatableStep<Context, Void> {
   private Consumer<Context> consumer;

   public CloseStep(String name, String flowName, Consumer<Context> consumer) {
      this(name, flowName, consumer, (Predicate)null);
   }

   public CloseStep(String name, String flowName, Consumer<Context> consumer, Predicate<Context> predicate) {
      super(name, flowName, predicate);
      this.consumer = consumer;
   }

   public FlowResultHolder execute(Context context) {
      try {
         if (this.consumer != null) {
            this.consumer.accept(context);
         }

         return FlowResultHolder.empty();
      } catch (RuntimeException e) {
         throw e;
      }
   }
}
