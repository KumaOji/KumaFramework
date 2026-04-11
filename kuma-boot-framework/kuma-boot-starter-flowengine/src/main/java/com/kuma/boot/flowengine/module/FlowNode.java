package com.kuma.boot.flowengine.module;

import com.kuma.boot.flowengine.engine.Execution;
import com.kuma.boot.flowengine.engine.StandardNodeExecution;
import jakarta.validation.constraints.NotBlank;

public abstract class FlowNode extends AbstractNode {
   private boolean traceLog;
   private @NotBlank String name;
   private String triggerClass;
   private RetryNode retryNode;

   public FlowNode() {
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getTriggerClass() {
      return this.triggerClass;
   }

   public void setTriggerClass(String triggerClass) {
      this.triggerClass = triggerClass;
   }

   public boolean isTraceLog() {
      return this.traceLog;
   }

   public void setTraceLog(boolean traceLog) {
      this.traceLog = traceLog;
   }

   public void execute(Execution execution) {
      (new StandardNodeExecution(execution, this)).execute();
   }

   public RetryNode getRetryNode() {
      return this.retryNode;
   }

   public void setRetryNode(RetryNode retryNode) {
      this.retryNode = retryNode;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("FlowNode{");
      sb.append("getName=").append(this.getName());
      sb.append("getTriggerClass=").append(this.getTriggerClass());
      sb.append('}');
      return sb.toString();
   }
}
