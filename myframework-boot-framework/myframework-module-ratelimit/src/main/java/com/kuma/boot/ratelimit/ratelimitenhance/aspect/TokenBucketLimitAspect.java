/*
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 *  org.aspectj.lang.annotation.Pointcut
 *  org.aspectj.lang.reflect.MethodSignature
 *  org.springframework.core.annotation.Order
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.ratelimit.ratelimitenhance.aspect;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.ratelimit.ratelimitenhance.annotation.TokenBucketLimit;
import com.kuma.boot.ratelimit.ratelimitenhance.exception.EnhanceRedisLimitException;
import com.kuma.boot.ratelimit.ratelimitenhance.helper.RedisLimitHelper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(value=0)
@Aspect
@Component
public class TokenBucketLimitAspect {
    private static final String DOT = ".";
    private final RedisLimitHelper redisLimitHelper;

    public TokenBucketLimitAspect(RedisLimitHelper redisLimitHelper) {
        this.redisLimitHelper = redisLimitHelper;
    }

    @Pointcut(value="@annotation(com.kuma.boot.ratelimit.ratelimitenhance.annotation.TokenBucketLimit)")
    public void aspect() {
    }

    @Around(value="aspect() && @annotation(limit)")
    public Object interceptor(ProceedingJoinPoint proceedingJoinPoint, TokenBucketLimit limit) throws Throwable {
        Object limitKey;
        if (StringUtils.isBlank((String)limit.limitKey())) {
            MethodSignature signature = (MethodSignature)proceedingJoinPoint.getSignature();
            limitKey = signature.getDeclaringTypeName() + DOT + signature.getMethod().getName();
        } else {
            limitKey = limit.limitKey();
        }
        Boolean pass = this.redisLimitHelper.tokenLimit((String)limitKey, limit.capacity(), limit.permits(), limit.rate());
        if (!pass.booleanValue()) {
            throw new EnhanceRedisLimitException("Congestion requested, please try again later.");
        }
        Object result = proceedingJoinPoint.proceed();
        return result;
    }
}

