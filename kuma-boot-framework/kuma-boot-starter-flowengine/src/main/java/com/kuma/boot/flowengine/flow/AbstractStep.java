package com.kuma.boot.flowengine.flow;

public abstract class AbstractStep<Context, Result> implements Step<Context, Result> {
   private String name;
   private String flowName;

   public AbstractStep(String name, String flowName) {
      this.name = name;
      this.flowName = flowName;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getFlowName() {
      return this.flowName;
   }

   public void setFlowName(String flowName) {
      this.flowName = flowName;
   }
}
