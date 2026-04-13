package com.kuma.boot.core.aop;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.CustomizableTraceInterceptor;
import org.springframework.aop.interceptor.SimpleTraceInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

/**
 * FunctionTraceAop
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration
public class FunctionTraceAop {

    //该类是个综合性的类即可以记录方法执行时间也可以跟踪方法的调用，执行异常情况。
    //我们可以通过CustomizableTraceInterceptor来实现复杂的监控行为，所有的日志记录你都可以自定义，包括3个消息：
    //setEnterMessage：方法调用进入时
    //setExitMessage：方法调用退出时
    //setExceptionMessage：调用发生异常时
    //通过自定义可以选择性的重写上面3个方法来自定义日志消息。
    //默认是没有执行耗时，这里输出了方法调用前后及执行耗时情况。
    //@Bean
    public CustomizableTraceInterceptor customizableTraceInterceptor() {
        CustomizableTraceInterceptor interceptor = new CustomizableTraceInterceptor();
        interceptor.setExitMessage("Exiting method '" +
                CustomizableTraceInterceptor.PLACEHOLDER_METHOD_NAME
                + "' of class [" + CustomizableTraceInterceptor.PLACEHOLDER_TARGET_CLASS_NAME
                + "], 执行耗时：【" + CustomizableTraceInterceptor.PLACEHOLDER_INVOCATION_TIME + "】毫秒");
        interceptor.setUseDynamicLogger(true);

        return interceptor;
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean
    public Advisor functionTraceAdvisor() {
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setAdvice(new KmcSimpleTraceInterceptor(true));
        //advisor.setAdvice(customizableTraceInterceptor());

        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("@annotation(com.kuma.boot.core.aop.FunctionTrace)");
        advisor.setPointcut(pointcut);
        return advisor;
    }

    //可用来跟踪方法的调用情况（方法调用前后或异常发生时）
    //可用来记录方法执行的耗时情况
    //SimpleTraceInterceptor拦截器使用也是非常的简单，该拦截主要就是用来跟踪方目标方法执行前后（或发生异常时）进行日志的记录。
    //
    public static class KmcSimpleTraceInterceptor extends SimpleTraceInterceptor {

        public KmcSimpleTraceInterceptor() {
            super();
        }

        public KmcSimpleTraceInterceptor( boolean useDynamicLogger ) {
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
