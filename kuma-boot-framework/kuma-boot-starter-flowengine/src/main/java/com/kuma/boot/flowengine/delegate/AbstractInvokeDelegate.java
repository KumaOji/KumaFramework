package com.kuma.boot.flowengine.delegate;

public abstract class AbstractInvokeDelegate implements InvokeDelegate {
   private Object target;

   public AbstractInvokeDelegate(Object target) {
      this.target = target;
   }

   public Object getTarget() {
      return this.target;
   }
}
