package com.kuma.boot.flowengine.module;

import com.kuma.boot.flowengine.engine.Execution;
import com.kuma.boot.flowengine.exception.FlowException;

public class NodeRef extends ActivityNode {
   private FlowNode flowNode;

   public NodeRef(String ref) {
      this.setName(ref);
   }

   public void execute(Execution execution) {
      if (this.flowNode == null) {
         Flow flow = execution.getCurrentFlow();
         throw new FlowException(String.format("flow=%s,version=%s\u6d41\u7a0b\u5b9a\u4e49\u4e2d\u4e0d\u5b58\u5728transition\u7684\u5f15\u7528\u8282\u70b9ref=%s", flow.getName(), flow.getVersion(), this.getName()));
      } else {
         this.flowNode.execute(execution);
      }
   }

   public void initialize(Flow flow) {
      if (this.flowNode == null) {
         for(AbstractNode node : flow.getNodes()) {
            String nodeName = ((ActivityNode)node).getName();
            if (nodeName != null && nodeName.equals(this.getName())) {
               this.flowNode = (FlowNode)node;
               this.flowNode.initialize(flow);
               return;
            }
         }

         EndNode endNode = flow.getEndNode();
         if (endNode.getName().equals(this.getName())) {
            this.flowNode = endNode;
         }

         if (this.flowNode == null) {
            throw new FlowException(String.format("flow=%s,version=%s,node=%s\u5f13\u7528\u4e0d\u5b58\u5728..", flow.getName(), flow.getVersion(), this.getName()));
         }
      }

   }

   public FlowNode getFlowNode() {
      return this.flowNode;
   }

   public String getTriggerClass() {
      return this.flowNode == null ? null : this.flowNode.getTriggerClass();
   }

   public void setTriggerClass(String triggerClass) {
      if (this.flowNode == null) {
         throw new FlowException("NodeRef\u8d4b\u4e88Triggerclass\u51fa\u9519flowNode = null");
      } else {
         this.flowNode.setTriggerClass(triggerClass);
      }
   }
}
