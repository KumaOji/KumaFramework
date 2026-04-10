package com.kuma.boot.logger.eden.access.aop;

import com.kuma.boot.logger.eden.access.config.AccessLogConfig;
import com.kuma.boot.logger.eden.access.util.AccessLogHelper;
import java.time.Duration;
import java.time.Instant;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.support.AopUtils;

public class AccessLogInterceptor implements MethodInterceptor {
   private final AccessLogConfig config;

   public AccessLogInterceptor(AccessLogConfig config) {
      this.config = config;
   }

   public Object invoke(@NotNull MethodInvocation invocation) throws Throwable {
      if (AopUtils.isAopProxy(invocation.getThis())) {
         return invocation.proceed();
      } else if (!AccessLogHelper.shouldLog(this.config.getSampleRate())) {
         return invocation.proceed();
      } else {
         Instant start = Instant.now();
         Object result = null;
         Throwable throwable = null;
         boolean var13 = false;

         Object var5;
         try {
            var13 = true;
            result = invocation.proceed();
            var5 = result;
            var13 = false;
         } catch (Throwable t) {
            throwable = t;
            throw t;
         } finally {
            if (var13) {
               long duration = Duration.between(start, Instant.now()).toMillis();
               AccessLogHelper.log(invocation, result, throwable, duration, this.config.isEnabledMdc(), this.config.getMaxLength(), this.config.getSlowThreshold());
            }
         }

         long duration = Duration.between(start, Instant.now()).toMillis();
         AccessLogHelper.log(invocation, result, throwable, duration, this.config.isEnabledMdc(), this.config.getMaxLength(), this.config.getSlowThreshold());
         return var5;
      }
   }
}
