/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.annotation.Around
 */
package com.kuma.boot.cache.redis.autoconfigure;

import java.util.Arrays;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

public class RedisCommandAspect {
    @Around(value="execution(* org.springframework.data.redis.core.RedisTemplate.*(..))")
    public Object monitorRedisCommands(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        System.out.println("Executing Redis command: " + methodName + " with args: " + Arrays.toString(args));
        return joinPoint.proceed();
    }
}

