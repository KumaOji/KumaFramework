/*
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.Signature
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 *  org.aspectj.lang.annotation.Pointcut
 *  org.aspectj.lang.reflect.MethodSignature
 */
package com.kuma.boot.ratelimit.ratelimitsnowjean.config.annotation.aspect;

import com.kuma.boot.ratelimit.ratelimitsnowjean.config.annotation.entity.Limiter;
import com.kuma.boot.ratelimit.ratelimitsnowjean.core.limiter.RateLimiter;
import com.kuma.boot.ratelimit.ratelimitsnowjean.core.observer.RateLimiterObserver;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class RateLimiterAspect {
    @Pointcut(value="@annotation(com.kuma.boot.ratelimit.ratelimitsnowjean.snowjeanspringbootstarter.annotation.entity.Limiter)")
    public void pointcut() {
    }

    @Around(value="pointcut() && @annotation(limiter)")
    public Object around(ProceedingJoinPoint pjp, Limiter limiter) throws Throwable {
        RateLimiter rateLimiter = RateLimiterObserver.getMap().get(limiter.value());
        if (rateLimiter.tryAcquire()) {
            return pjp.proceed();
        }
        Signature sig = pjp.getSignature();
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("This annotation can only be used in methods.");
        }
        MethodSignature msg = (MethodSignature)sig;
        Object target = pjp.getTarget();
        Method fallback = target.getClass().getMethod(limiter.fallback(), msg.getParameterTypes());
        return fallback.invoke(target, pjp.getArgs());
    }
}

