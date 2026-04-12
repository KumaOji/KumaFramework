package com.kuma.boot.webagg.aop;

import com.kuma.boot.webagg.controller.BusinessController;
import com.kuma.boot.webagg.controller.InnerController;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

public class RecordValidationAdvisor extends AbstractPointcutAdvisor implements Ordered {

   private final RecordValidationInterceptor interceptor;
   private final Pointcut pointcut;
   private int order = Ordered.HIGHEST_PRECEDENCE + 100;

   public RecordValidationAdvisor( RecordValidationInterceptor interceptor ) {
      Assert.notNull(interceptor, "Interceptor must not be null");
      this.interceptor = interceptor;
      this.order = interceptor.getOrder();

      // 【核心修改】自定义 Pointcut
      this.pointcut = new StaticMethodMatcherPointcut() {
         @Override
         public boolean matches( Method method, Class<?> targetClass ) {
            // 1. 类级别过滤：检查 targetClass 是否继承自 BusinessController 或 InnerController
            if (!isTargetController(targetClass)) {
               return false;
            }

            // 2. 方法级别过滤：通常我们只拦截 public 方法
            // 如果你想拦截所有 public 方法，直接返回 true
            // 如果想排除 toString/equals 等，可以加判断
            return method.getDeclaringClass() != Object.class &&
                    java.lang.reflect.Modifier.isPublic(method.getModifiers());
         }

         // 辅助方法：判断类是否继承自指定父类
         private boolean isTargetController( Class<?> clazz ) {
            if (clazz == null) {
               return false;
            }
            // 检查是否直接继承或间接继承
            return BusinessController.class.isAssignableFrom(clazz) ||
                    InnerController.class.isAssignableFrom(clazz);
         }

         @Override
         public ClassFilter getClassFilter() {
            // 优化：在类加载阶段就先过滤一遍，提高性能
            return clazz -> BusinessController.class.isAssignableFrom(clazz) ||
                    InnerController.class.isAssignableFrom(clazz);
         }
      };
   }

   @Override
   public Pointcut getPointcut() {
      return this.pointcut;
   }

   @Override
   public RecordValidationInterceptor getAdvice() {
      return this.interceptor;
   }

   @Override
   public int getOrder() {
      return this.order;
   }
}
