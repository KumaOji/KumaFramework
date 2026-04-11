package com.kuma.boot.flowengine.simpleflow.api;

import java.util.Map;

public abstract class ExecutableNode {
   public ExecutableNode() {
   }

   public abstract void execute(Map<String, Object> context);

   public String getName() {
      return this.getClass().getSimpleName();
   }

   public String getDescription() {
      return "Executable node: " + this.getName();
   }

   public void prepare(Map<String, Object> context) {
   }

   public void cleanup(Map<String, Object> context) {
   }

   public boolean validate(Map<String, Object> context) {
      return true;
   }
}
