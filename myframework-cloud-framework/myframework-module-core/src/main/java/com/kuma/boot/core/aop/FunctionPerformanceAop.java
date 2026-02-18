package com.kuma.boot.core.aop;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.PerformanceMonitorInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Role;

/**
 * FunctionPerformanceAop
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Aspect
@AutoConfiguration
@EnableAspectJAutoProxy
public class FunctionPerformanceAop {

    @Pointcut("@annotation(com.kuma.boot.core.aop.FunctionPerformance)")
    public void functionPerformance() {
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor functionPerformanceAdvisor() {
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setAdvice(new KmcPerformanceMonitorInterceptor(true));
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("@annotation(com.kuma.boot.core.aop.FunctionPerformance)");
        advisor.setPointcut(pointcut);
        return advisor;
    }

    //性能监控拦截器
    //可用来记录方法执行的耗时情况
    //PerformanceMonitorInterceptor 拦截器，它可以与任何自定义方法相关联，以便在相同的时间点执行。
    // 这个类使用了一个 StopWatch 实例来确定方法运行的起始和结束时间。
    public static class KmcPerformanceMonitorInterceptor extends PerformanceMonitorInterceptor {

        public KmcPerformanceMonitorInterceptor() {
            super();
        }

        public KmcPerformanceMonitorInterceptor( boolean useDynamicLogger ) {
            super(useDynamicLogger);
        }

        @Override
        protected Object invokeUnderTrace( MethodInvocation invocation, Log logger )
                throws Throwable {
            //如果你对默认的日志输出不满意，你完全可以通过重写父类的invokeUnderTrace方法，如下默认实现

            return super.invokeUnderTrace(invocation, logger);
        }
    }
}
