package com.kuma.boot.flowengine.module;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public abstract class ActivityNode extends FlowNode {
   private @NotNull @Valid Condition condition;

   public ActivityNode() {
   }

   public Condition getCondition() {
      return this.condition;
   }

   public void setCondition(Condition condition) {
      this.condition = condition;
      this.condition.setActivityNode(this);
   }

   public void initialize(Flow flow) {
      this.getCondition().initialize(flow);
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("ActivityNode{");
      sb.append("name=").append(this.getName());
      sb.append("getTriggerClass=").append(this.getTriggerClass());
      sb.append("condition=").append(this.condition);
      sb.append('}');
      return sb.toString();
   }
}
