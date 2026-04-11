package com.kuma.boot.flowengine.engine;

public enum ExecutionStatus {
   SUSPEND,
   FAIL,
   SUCCESS;

   private ExecutionStatus() {
   }

   // $FF: synthetic method
   private static ExecutionStatus[] $values() {
      return new ExecutionStatus[]{SUSPEND, FAIL, SUCCESS};
   }
}
