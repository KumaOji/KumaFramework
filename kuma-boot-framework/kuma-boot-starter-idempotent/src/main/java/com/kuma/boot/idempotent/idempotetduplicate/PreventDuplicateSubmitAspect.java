/*
 *  com.kuma.boot.common.utils.servlet.RequestUtils
 *  jakarta.servlet.http.HttpServletRequest
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 *  org.redisson.api.RLock
 *  org.redisson.api.RedissonClient
 *  org.springframework.core.annotation.Order
 *  org.springframework.stereotype.Component
 *  org.springframework.web.context.request.RequestContextHolder
 *  org.springframework.web.context.request.ServletRequestAttributes
 */
package com.kuma.boot.idempotent.idempotetduplicate;

import com.kuma.boot.core.utils.servlet.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Order(value=0)
@Component
@ConditionalOnBean(RedissonClient.class)
public class PreventDuplicateSubmitAspect {
    private final RedissonClient redissonClient;

    public PreventDuplicateSubmitAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Around(value="@annotation(preventDuplicateSubmit)")
    public Object preventDuplicateSubmit(ProceedingJoinPoint joinPoint, PreventDuplicateSubmit preventDuplicateSubmit) throws Throwable {
        String prefixKey = this.generateResubmitLockKey(joinPoint, preventDuplicateSubmit);
        RLock lock = this.redissonClient.getLock(prefixKey);
        if (!lock.tryLock(0L, (long)preventDuplicateSubmit.expire(), TimeUnit.SECONDS)) {
            // empty if block
        }
        return joinPoint.proceed();
    }

    private String generateResubmitLockKey(ProceedingJoinPoint joinPoint, PreventDuplicateSubmit preventDuplicateSubmit) {
        Object prefix = "";
        Object[] args = joinPoint.getArgs();
        DuplicateTypeEnum type = preventDuplicateSubmit.type();
        switch (type) {
            case USER_ID: {
                break;
            }
            case USER_NAME: {
                break;
            }
            case ARGS: {
                prefix = Arrays.deepToString(args);
                break;
            }
            default: {
                HttpServletRequest request = ((ServletRequestAttributes)Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
                prefix = RequestUtils.getHttpServletRequestIpAddress((HttpServletRequest)request);
            }
        }
        if (!preventDuplicateSubmit.global()) {
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();
            prefix = (String)prefix + ":" + className + ":" + methodName;
        }
        return "PREVENT_DUPLICATE_SUBMIT_PREFIX" + (String)prefix;
    }
}

