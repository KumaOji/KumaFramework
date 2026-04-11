package com.kuma.boot.eventbus.disruptor.tmp5.task.rejected;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ThreadPoolExecutor;

public class RejectedInvocationHandler implements InvocationHandler, RejectedAware {
   private final Object target;

   public RejectedInvocationHandler(Object target) {
      this.target = target;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      try {
         ThreadPoolExecutor executor = (ThreadPoolExecutor)args[1];
         this.beforeReject(executor);
         return method.invoke(this.target, args);
      } catch (InvocationTargetException ex) {
         throw ex.getCause();
      }
   }
}
