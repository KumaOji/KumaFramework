package com.kuma.boot.flowengine.flow;

import java.util.LinkedList;
import java.util.function.Predicate;

public class ConditionalStepBuilder<Context, Result> {
   private final FlowDefinitionBuilder<Context, Result> flow;
   private String name;
   private LinkedList<Tuple<Context, Result>> conditionMap = new LinkedList();
   private Predicate<Context> currentCondition;
   private FlowDefinition<Context, Result> otherwiseStep = null;

   public ConditionalStepBuilder(String name, FlowDefinitionBuilder<Context, Result> flow) {
      this.flow = flow;
      this.name = name;
   }

   public FlowDefinitionBuilder<Context, Result> when(String name, Predicate<Context> predicate) {
      this.currentCondition = predicate;
      return new FlowDefinitionBuilder<Context, Result>(name, this);
   }

   public FlowDefinitionBuilder<Context, Result> otherwise(String name) {
      this.currentCondition = null;
      return new FlowDefinitionBuilder<Context, Result>(name, this);
   }

   public FlowDefinitionBuilder<Context, Result> finish() {
      this.flow.addStep(new ConditionalStep(this.name, this.flow.getName(), this.conditionMap, this.otherwiseStep));
      return this.flow;
   }

   ConditionalStepBuilder accept(FlowDefinitionBuilder<Context, Result> subFlow) {
      if (this.currentCondition == null) {
         this.otherwiseStep = subFlow.build();
      } else {
         this.conditionMap.add(new Tuple(this.currentCondition, subFlow.build()));
      }

      return this;
   }

   public static final class Tuple<Context, Result> {
      Predicate<Context> predicate;
      Step<Context, Result> subFlow;

      public Tuple(Predicate<Context> predicate, Step<Context, Result> subFlow) {
         this.predicate = predicate;
         this.subFlow = subFlow;
      }

      public Predicate<Context> getPredicate() {
         return this.predicate;
      }

      public void setPredicate(Predicate<Context> predicate) {
         this.predicate = predicate;
      }

      public Step<Context, Result> getSubFlow() {
         return this.subFlow;
      }

      public void setSubFlow(Step<Context, Result> subFlow) {
         this.subFlow = subFlow;
      }
   }
}
