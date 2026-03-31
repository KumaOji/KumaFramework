package com.kuma.boot.monitor.monitor.monitor.utils;

import com.kuma.boot.monitor.monitor.monitor.Monitor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class MetricsMethodInterceptor implements MethodInterceptor {
   private String METRICS_KEY;

   public MetricsMethodInterceptor(String metricsKey) {
      this.METRICS_KEY = metricsKey;
   }

   public Object invoke(MethodInvocation invocation) throws Throwable {
      if (Object.class.equals(invocation.getMethod().getDeclaringClass())) {
         return invocation.proceed();
      } else {
         Monitor.TimeContext timeContext = Monitor.timer(this.METRICS_KEY, invocation.getMethod().getName());

         Object var3;
         try {
            var3 = invocation.proceed();
         } catch (Exception e) {
            timeContext.error();
            throw e;
         } finally {
            timeContext.end();
         }

         return var3;
      }
   }
}
