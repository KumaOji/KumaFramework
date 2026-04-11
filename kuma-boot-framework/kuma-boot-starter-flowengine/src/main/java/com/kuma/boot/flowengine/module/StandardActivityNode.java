package com.kuma.boot.flowengine.module;

public class StandardActivityNode extends ActivityNode {
   private NodeType nodeType;

   public StandardActivityNode() {
   }

   public NodeType getNodeType() {
      return this.nodeType;
   }

   public void setNodeType(NodeType nodeType) {
      this.nodeType = nodeType;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("RetryNode{");
      sb.append("nodeType=").append(this.nodeType);
      sb.append("getTriggerClass=").append(this.getTriggerClass());
      sb.append("getCondition=").append(this.getCondition());
      sb.append('}');
      return sb.toString();
   }
}
