package com.kuma.boot.webagg.aop;

import com.kuma.boot.ddd.model.types.MarkerCheck;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.Ordered;

public class RecordValidationInterceptor implements MethodInterceptor, Ordered {
   private int order = -2147483548;

   public RecordValidationInterceptor() {
   }

   public int getOrder() {
      return this.order;
   }

   public void setOrder(int order) {
      this.order = order;
   }

   public Object invoke(MethodInvocation invocation) throws Throwable {
      Object[] args = invocation.getArguments();

      for(Object arg : args) {
         if (arg instanceof MarkerCheck marker) {
            try {
               marker.check();
            } catch (Exception e) {
               throw new IllegalArgumentException("DTO \u6821\u9a8c\u5931\u8d25: " + e.getMessage(), e);
            }
         }
      }

      return invocation.proceed();
   }
}
