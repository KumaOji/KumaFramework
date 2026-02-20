package com.kuma.boot.cache.redis.autoconfigure;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

//控Redis命令流可能会对性能产生一定影响，尤其是使用MONITOR命令时。所以，笔者建议在开发或调试环境中启用实时命令流监控，而
// 在生产环境中应避免长时间启用MONITOR，从而避免影响到Redis的操作。
//使用AOP方式监控RedisTemplate方法时，虽然不会影响Redis本身，
// 但会对应用的性能产生一定的开销，因此应根据需要调整日志级别，避免打印过多的日志信息。
//@Aspect
//@Component
/**
 * RedisCommandAspect
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class RedisCommandAspect {

    @Around("execution(* org.springframework.data.redis.core.RedisTemplate.*(..))")
    public Object monitorRedisCommands( ProceedingJoinPoint joinPoint ) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // 打印出Redis操作的信息
        System.out.println("Executing Redis command: " + methodName + " with args: " + Arrays.toString(args));

        // 执行Redis操作
        return joinPoint.proceed();
    }
}
