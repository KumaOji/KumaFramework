package com.kuma.boot.webagg.aop;

import com.kuma.boot.webagg.controller.BusinessController;
import com.kuma.boot.webagg.controller.InnerController;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

public class RecordValidationAdvisor extends AbstractPointcutAdvisor implements Ordered {
   private final RecordValidationInterceptor interceptor;
   private final Pointcut pointcut;
   private int order = -2147483548;

   public RecordValidationAdvisor(RecordValidationInterceptor interceptor) {
      Assert.notNull(interceptor, "Interceptor must not be null");
      this.interceptor = interceptor;
      this.order = interceptor.getOrder();
      this.pointcut = new StaticMethodMatcherPointcut() {
         {
            Objects.requireNonNull(RecordValidationAdvisor.this);
         }

         public boolean matches(Method method, Class<?> targetClass) {
            if (!this.isTargetController(targetClass)) {
               return false;
            } else {
               return method.getDeclaringClass() != Object.class && Modifier.isPublic(method.getModifiers());
            }
         }

         private boolean isTargetController(Class<?> clazz) {
            if (clazz == null) {
               return false;
            } else {
               return BusinessController.class.isAssignableFrom(clazz) || InnerController.class.isAssignableFrom(clazz);
            }
         }

         public ClassFilter getClassFilter() {
            return (clazz) -> BusinessController.class.isAssignableFrom(clazz) || InnerController.class.isAssignableFrom(clazz);
         }
      };
   }

   public Pointcut getPointcut() {
      return this.pointcut;
   }

   public RecordValidationInterceptor getAdvice() {
      return this.interceptor;
   }

   public int getOrder() {
      return this.order;
   }
}
