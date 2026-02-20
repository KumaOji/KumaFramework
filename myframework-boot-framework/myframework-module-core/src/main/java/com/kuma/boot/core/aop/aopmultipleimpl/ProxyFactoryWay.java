package com.kuma.boot.core.aop.aopmultipleimpl;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;

/**
 * ProxyFactoryWay
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class ProxyFactoryWay {

    public static interface CommonDAO {

    }

    public static class PersonService {

        public void save() {
            System.out.println("save method invoke...");
        }
    }

    public static void main( String[] args ) {
        ProxyFactory factory = new ProxyFactory(new PersonService());
        factory.setProxyTargetClass(true);
        // 设置通知类（内部会自动的包装为Advisor）
        factory.addAdvice(new MethodInterceptor() {
            @Override
            public Object invoke( MethodInvocation invocation ) throws Throwable {
                System.out.println("权限控制...");
                return invocation.proceed();
            }
        });
        factory.addAdvisor(new PointcutAdvisor() {
            @Override
            public Advice getAdvice() {
                return new MethodInterceptor() {
                    @Override
                    public Object invoke( MethodInvocation invocation ) throws Throwable {
                        System.out.println("日志记录...");
                        return invocation.proceed();
                    }
                };
            }

            @Override
            public Pointcut getPointcut() {
                return new StaticMethodMatcherPointcut() {
                    @Override
                    public boolean matches( Method method, Class<?> targetClass ) {
                        return method.getName().equals("save");
                    }
                };
            }
        });
        PersonService ps = (PersonService) factory.getProxy();
        ps.save();
    }
}
