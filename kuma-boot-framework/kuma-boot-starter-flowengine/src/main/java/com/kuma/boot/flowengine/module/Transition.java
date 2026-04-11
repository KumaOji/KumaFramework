package com.kuma.boot.flowengine.module;

import com.kuma.boot.flowengine.engine.Execution;
import com.kuma.boot.flowengine.module.validate.NodeRefConstraint;
import jakarta.validation.constraints.NotNull;

public class Transition extends AbstractNode {
   private @NotNull String event;
   private @NotNull String description;
   private @NotNull FlowNode from;
   @NodeRefConstraint
   private NodeRef to;

   public Transition() {
   }

   public void initialize(Flow flow) {
      this.to.initialize(flow);
   }

   public void execute(Execution execution) {
      FlowNode flowNode = this.to.getFlowNode();
      if (!(flowNode instanceof StandardActivityNode) || ((StandardActivityNode)flowNode).getNodeType() != NodeType.ACTIVE_NODE) {
         this.to.execute(execution);
      }
   }

   private void retryListen(Execution execution) {
      if (this.exitRetry(execution)) {
         execution.getEngine().getListenerDelegateContext().action(execution, "retry_exit");
      }

   }

   private boolean fromTarget() {
      return null != this.from.getRetryNode() && this.to.getFlowNode() instanceof RetryNode;
   }

   private boolean toTarget() {
      return this.from instanceof RetryNode && null != this.to.getFlowNode().getRetryNode();
   }

   private boolean exitRetry(Execution execution) {
      return null != execution.getFlowTrace() && null != this.from.getRetryNode() && !(this.to.getFlowNode() instanceof RetryNode);
   }

   private boolean endRetry() {
      return "retry_end".equals(this.event);
   }

   public String getEvent() {
      return this.event;
   }

   public void setEvent(String event) {
      this.event = event;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public FlowNode getFrom() {
      return this.from;
   }

   public void setFrom(FlowNode from) {
      this.from = from;
   }

   public NodeRef getTo() {
      return this.to;
   }

   public void setTo(NodeRef to) {
      this.to = to;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("Transition{");
      sb.append("description=").append(this.description);
      sb.append("event=").append(this.event);
      sb.append("from=").append(this.from.getName());
      sb.append("to=").append(this.to);
      sb.append('}');
      return sb.toString();
   }
}
