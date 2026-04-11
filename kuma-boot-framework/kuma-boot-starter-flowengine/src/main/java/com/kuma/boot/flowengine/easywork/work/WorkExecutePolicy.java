package com.kuma.boot.flowengine.easywork.work;

public enum WorkExecutePolicy {
   FAST_FAIL,
   FAST_FAIL_EXCEPTION,
   FAST_SUCCESS,
   FAST_ALL,
   FAST_ALL_SUCCESS,
   FAST_EXCEPTION;

   private WorkExecutePolicy() {
   }

   // $FF: synthetic method
   private static WorkExecutePolicy[] $values() {
      return new WorkExecutePolicy[]{FAST_FAIL, FAST_FAIL_EXCEPTION, FAST_SUCCESS, FAST_ALL, FAST_ALL_SUCCESS, FAST_EXCEPTION};
   }
}
