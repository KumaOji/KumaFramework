package com.kuma.boot.flowengine.easywork.work;

public abstract class NamedConditionWork extends NamedWork {
   protected Work work;

   public NamedConditionWork() {
   }

   public Work getWork() {
      return this.work;
   }
}
