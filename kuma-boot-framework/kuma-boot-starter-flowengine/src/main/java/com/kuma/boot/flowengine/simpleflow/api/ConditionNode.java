package com.kuma.boot.flowengine.simpleflow.api;

import java.util.Map;

public abstract class ConditionNode {
   public ConditionNode() {
   }

   public abstract boolean evaluate(Map<String, Object> context);

   public String getName() {
      return this.getClass().getSimpleName();
   }

   public String getDescription() {
      return "Condition node: " + this.getName();
   }

   public String getConditionExpression() {
      return "Custom condition in " + this.getName();
   }

   public void prepare(Map<String, Object> context) {
   }

   public boolean validate(Map<String, Object> context) {
      return true;
   }
}
