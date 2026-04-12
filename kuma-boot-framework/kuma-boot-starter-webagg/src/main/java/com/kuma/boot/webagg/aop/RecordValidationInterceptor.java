package com.kuma.boot.webagg.aop;

import com.kuma.boot.ddd.model.types.MarkerCheck;
import com.kuma.boot.ddd.model.types.MarkerInterface;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.Ordered;

public class RecordValidationInterceptor implements MethodInterceptor, Ordered {

   // 定义优先级：越高越先执行 (数值越小优先级越高)
   // 我们希望校验尽早执行，所以在事务(@Transactional order=0) 之前
   // Ordered.HIGHEST_PRECEDENCE = Integer.MIN_VALUE (-2147483648)
   private int order = Ordered.HIGHEST_PRECEDENCE + 100;

   @Override
   public int getOrder() {
      return this.order;
   }

   public void setOrder( int order ) {
      this.order = order;
   }

   @Override
   public Object invoke( MethodInvocation invocation ) throws Throwable {
      // 1. 获取参数
      Object[] args = invocation.getArguments();

      // 2. 遍历参数，寻找实现了 MarkerInterface 的对象
      for (Object arg : args) {
         if (arg instanceof MarkerCheck marker) {
            try {
               // 【核心】执行 record 中的 default 方法
               marker.check();
            } catch (Exception e) {
               // 校验失败，抛出异常中断流程
               // 这里抛出的异常会被 Spring MVC 的全局异常处理器捕获
               throw new IllegalArgumentException("DTO 校验失败: " + e.getMessage(), e);
            }
         }
      }

      // 3. 校验通过，继续执行链条中的下一个拦截器或目标方法
      return invocation.proceed();
   }
}
