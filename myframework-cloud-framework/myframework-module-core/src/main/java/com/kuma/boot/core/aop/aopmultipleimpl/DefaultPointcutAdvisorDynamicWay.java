package com.kuma.boot.core.aop.aopmultipleimpl;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.adapter.AdvisorAdapter;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
 * DefaultPointcutAdvisorDynamicWay
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class DefaultPointcutAdvisorDynamicWay {

    @Configuration
    public static class WebmvcConfig {

        @Bean
        public Pointcut customPointCut() {
            GreetingDynamicPointcut greetingDynamicPointcut = new GreetingDynamicPointcut();
            // 动态配置只需要把这里的execution 字符串替换成自己从配置文件中获取的配置即可。
            greetingDynamicPointcut.setExpression(
                    "execution(public * com.allens.test.controller..*(..)) || execution(public * com.allens.test.service..*(..))");
            return greetingDynamicPointcut;
        }

        @Bean
        GreetingAdvice getAdvice() {
            return new GreetingAdvice();
        }

        // ②
        @Bean
        DefaultPointcutAdvisor defaultPointcutAdvisor() {
            DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor();
            defaultPointcutAdvisor.setPointcut(customPointCut());
            defaultPointcutAdvisor.setAdvice(getAdvice());
            return defaultPointcutAdvisor;
        }
    }


    public static class GreetingInterceptor implements MethodInterceptor {

        @Override
        public Object invoke( MethodInvocation methodInvocation ) throws Throwable {
            try {
                System.out.println("调用前...");
                return methodInvocation.proceed();
            } catch (Exception e) {
                System.out.println("error");
                throw e;
            } finally {
                System.out.println("调用后...");
            }
        }
    }

    public static class GreetingAdvice extends GreetingInterceptor implements MethodBeforeAdvice,
            AfterReturningAdvice,
            AdvisorAdapter {

        @Override
        public void before( Method method, Object[] args, Object target )
                throws Throwable {
            // 输出切点
            System.out.println("Pointcut:" + target.getClass().getName() + "."
                    + method.getName());
            if (args.length > 0) {
                String clientName = (String) args[0];
                System.out.println("How are you " + clientName + " ?");
            }
        }

        @Override
        public void afterReturning( Object returnValue, Method method, Object[] args, Object target )
                throws Throwable {
            System.out.println(returnValue);
        }

        @Override
        public boolean supportsAdvice( Advice advice ) {
            return true;
        }

        @Override
        public MethodInterceptor getInterceptor( Advisor advisor ) {
            return null;
        }
    }


    public static class GreetingDynamicPointcut extends AspectJExpressionPointcut {

    }


}
