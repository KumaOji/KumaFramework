/*
 *  com.kuma.boot.common.utils.aop.AopUtils
 *  org.aspectj.lang.JoinPoint
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 *  org.aspectj.lang.annotation.Pointcut
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.ratelimit.ratelimitbs.spring.aop;

import com.kuma.boot.common.utils.aop.AopUtils;
import com.kuma.boot.ratelimit.ratelimitbs.core.bs.RateLimitBs;
import java.lang.reflect.Method;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RateLimitAspect {
    private final RateLimitBs rateLimitBs;

    public RateLimitAspect(RateLimitBs rateLimitBs) {
        this.rateLimitBs = rateLimitBs;
    }

    @Pointcut(value="@annotation(com.kuma.boot.ratelimit.ratelimitbs.core.annotation.RateLimit) || @annotation(com.kuma.boot.ratelimit.ratelimitbs.core.annotation.RateLimits)")
    public void methodMyPointcut() {
    }

    @Pointcut(value="@within(com.kuma.boot.ratelimit.ratelimitbs.core.annotation.RateLimit) || @within(com.kuma.boot.ratelimit.ratelimitbs.core.annotation.RateLimits)")
    public void classMyPointcut() {
    }

    @Around(value="methodMyPointcut() || classMyPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Method method = AopUtils.getMethod((JoinPoint)point);
        Object[] args = point.getArgs();
        this.rateLimitBs.tryAcquire(method, args);
        return point.proceed();
    }
}

