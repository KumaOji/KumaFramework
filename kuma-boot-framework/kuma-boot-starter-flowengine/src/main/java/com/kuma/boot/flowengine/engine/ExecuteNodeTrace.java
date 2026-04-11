package com.kuma.boot.flowengine.engine;

import com.kuma.boot.flowengine.module.FlowNode;
import com.kuma.boot.flowengine.module.RetryNode;
import java.util.HashSet;
import java.util.Set;

public class ExecuteNodeTrace {
   private NodeRef ref = new NodeRef();
   private Set<String> nodes = new HashSet();

   public ExecuteNodeTrace() {
   }

   void add(FlowNode node) {
      if (!(node instanceof RetryNode) && this.nodes.add(node.getName())) {
         this.ref.node = node;
         NodeRef t = this.ref;
         this.ref = new NodeRef();
         this.ref.parent = t;
      }

   }

   public NodeRef current() {
      return this.ref.parent;
   }

   public static class NodeRef {
      private FlowNode node;
      private NodeRef parent;

      public NodeRef() {
      }

      public FlowNode getNode() {
         return this.node;
      }

      public NodeRef getParent() {
         return this.parent;
      }
   }
}
