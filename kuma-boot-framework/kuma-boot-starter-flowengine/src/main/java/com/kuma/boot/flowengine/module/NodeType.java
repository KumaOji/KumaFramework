package com.kuma.boot.flowengine.module;

public enum NodeType {
   ACTIVE_NODE,
   AUTO_TASK,
   RETRY_TASK;

   private NodeType() {
   }

   public static NodeType get(String name) {
      NodeType snt = null;

      for(NodeType nodeType : values()) {
         if (nodeType.name().equals(adapt(name))) {
            snt = nodeType;
            break;
         }
      }

      return snt;
   }

   private static String adapt(String name) {
      return name.toUpperCase();
   }

   // $FF: synthetic method
   private static NodeType[] $values() {
      return new NodeType[]{ACTIVE_NODE, AUTO_TASK, RETRY_TASK};
   }
}
