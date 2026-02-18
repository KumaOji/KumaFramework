package com.kuma.boot.core.aop.aopmultipleimpl;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * DefaultPointcutAdvisorWay
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class DefaultPointcutAdvisorWay {

    @Configuration
    @ConditionalOnProperty(name = "aop.exception.pointcut", matchIfMissing = true)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static class ServiceAopConfig {

        // 带任何参数的service包下的接口
        @Value("${aop.pointcut:execution(* org.example.service..*(..))}")
        private String expression;

        public static final String traceExecution = "execution(* com.hfi.aop..*.*(..))";

        /**
         * 异常的切面
         */
        @Bean
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        public Advisor defaultPointcutAdvisor2() {
            //AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            //pointcut.setExpression(traceExecution);

            //AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(HfiTrace.class, true);
            //JdkRegexpMethodPointcut pointcut = new JdkRegexpMethodPointcut();
            //pointcut.setPatterns("com.hfi.*");

            // 声明切点
            //JdkRegexpMethodPointcut pointcut = new JdkRegexpMethodPointcut();
            //pointcut.setPatterns("com.example.*");
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            // 拦截org.example.service包和子包下带一个参数的任何方法
            //pointcut.setExpression("execution(* org.example.service..*(*))");
            pointcut.setExpression(expression);

            // 声明增强
            ServiceInterceptor interceptor = new ServiceInterceptor();

            // 配置切面
            DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();

            advisor.setPointcut(pointcut);
            advisor.setAdvice(interceptor);
            return advisor;
        }
    }


    public static class ServiceInterceptor implements MethodInterceptor {

        private Logger logger = LoggerFactory.getLogger(this.getClass());

        public ServiceInterceptor() {
        }

        public Object invoke( MethodInvocation invocation ) throws Throwable {
            long startTimeMillis = System.currentTimeMillis();
            long endTimeMillis = 0L;
            String className = invocation.getThis().getClass().getSimpleName();
            String interfaceFullName = invocation.getThis().getClass().getName();
            String methodName = invocation.getMethod().getName();
            String txName = "交易：" + className + "." + methodName;

            try {
                Class<?>[] interfaces = invocation.getThis().getClass().getInterfaces();
                if (interfaces != null && interfaces.length > 0) {
                    interfaceFullName = interfaces[0].getName();
                }
            } catch (Exception var15) {
                logger.warn("获取接口名称异常", var15);
            }

            Object[] args = invocation.getArguments();
            boolean noArgs = false;
            if (args == null || args.length < 1 || args[0] == null) {
                noArgs = true;
            }

            try {
                if (!noArgs) {
                    //CheckInputUtil.validate(args[0]);
                }

                Object returnObj = invocation.proceed();
                endTimeMillis = System.currentTimeMillis();
                logger.info(
                        txName + " success ,耗时：" + ( endTimeMillis - startTimeMillis ) + "ms");
                return returnObj;
            } catch (Exception var16) {
                endTimeMillis = System.currentTimeMillis();

                throw var16;
            }
        }
    }
}
