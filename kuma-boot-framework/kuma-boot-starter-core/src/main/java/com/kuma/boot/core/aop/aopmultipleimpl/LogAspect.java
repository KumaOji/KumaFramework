package com.kuma.boot.core.aop.aopmultipleimpl;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * LogAspect
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@Aspect
public class LogAspect {

    // 定义切点
    @Pointcut("execution(* com.pack..*.*(..))")
    public void log() {
    }

    // 前置通知
    @Before("log()")
    public void beforeLog() {
        System.out.println("记录日志Before...");
    }

    // 后置通知
    @After("log()")
    public void afterLog() {
        System.out.println("记录日志After");
    }

    // 异常通知
    @AfterThrowing(pointcut = "log()", throwing = "tx")
    public void ex( Throwable tx ) {
        System.err.println("发生异常: " + tx.getMessage());
    }

    // 环绕通知
    @Around("log() && args(name)")
    public Object around( ProceedingJoinPoint pjp, String name ) throws Throwable {
        System.out.println("log before...");
        System.out.println("name = " + name);
        Object ret = pjp.proceed();
        System.out.println("log after...");
        return ret;
    }
}
