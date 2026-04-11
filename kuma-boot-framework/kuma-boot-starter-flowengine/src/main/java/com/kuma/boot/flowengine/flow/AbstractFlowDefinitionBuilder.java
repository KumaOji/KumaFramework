package com.kuma.boot.flowengine.flow;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractFlowDefinitionBuilder<Context> {
   private List<Step<Context, ?>> steps = new LinkedList();
   private String name;

   void addStep(Step<Context, ?> step) {
      this.steps.add(step);
   }

   AbstractFlowDefinitionBuilder(String name) {
      this.name = name;
   }

   public List<Step<Context, ?>> getSteps() {
      return this.steps;
   }

   public void setStep(List<Step<Context, ?>> steps) {
      this.steps = steps;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
