package com.kuma.boot.flowengine.easywork.work;

public enum WorkStatus {
   COMPLETED,
   FAILED,
   STOPPED;

   private WorkStatus() {
   }

   // $FF: synthetic method
   private static WorkStatus[] $values() {
      return new WorkStatus[]{COMPLETED, FAILED, STOPPED};
   }
}
