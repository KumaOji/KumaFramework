package com.kuma.boot.data.jpa.autoconfigure;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.CustomizableTraceInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Role;

@AutoConfiguration
@EnableAspectJAutoProxy
@Role(2)
public class InterceptorAutoConfiguration {
   public InterceptorAutoConfiguration() {
   }

   @Bean
   @Role(2)
   public Advisor jpaTraceAdvisor() {
      CustomizableTraceInterceptor interceptor = new CustomizableTraceInterceptor();
      interceptor.setEnterMessage("Entering $[methodName]($[arguments]).");
      interceptor.setExitMessage("Leaving $[methodName](..) with return value $[returnValue], took $[invocationTime]ms.");
      interceptor.setUseDynamicLogger(true);
      interceptor.setLoggerName("com.kuma.boot.data.jpa.aop.TraceAspect");
      AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
      pointcut.setExpression("execution(public * org.springframework.data.repository.Repository+.*(..))");
      return new DefaultPointcutAdvisor(pointcut, interceptor);
   }
}
