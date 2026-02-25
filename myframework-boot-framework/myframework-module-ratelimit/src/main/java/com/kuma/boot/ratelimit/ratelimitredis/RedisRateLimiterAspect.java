/*
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.spel.ExpressionEvaluator
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 *  org.aspectj.lang.reflect.MethodSignature
 *  org.jspecify.annotations.NonNull
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.context.expression.AnnotatedElementKey
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.util.Assert
 */
package com.kuma.boot.ratelimit.ratelimitredis;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.spel.ExpressionEvaluator;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.Assert;

@Aspect
public class RedisRateLimiterAspect
implements ApplicationContextAware {
    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();
    private final RedisRateLimiterClient rateLimiterClient;
    private ApplicationContext applicationContext;

    public RedisRateLimiterAspect(RedisRateLimiterClient rateLimiterClient) {
        this.rateLimiterClient = rateLimiterClient;
    }

    @Around(value="@annotation(limiter)")
    public Object aroundRateLimiter(ProceedingJoinPoint point, RateLimiter limiter) throws Throwable {
        Object rateKey;
        String limitKey = limiter.value();
        Assert.hasText((String)limitKey, (String)"@RateLimiter value must have length; it must not be null or empty");
        String limitParam = limiter.param();
        if (StringUtils.isNotBlank((String)limitParam)) {
            String evalAsText = this.evalLimitParam(point, limitParam);
            rateKey = limitKey + ":" + evalAsText;
        } else {
            rateKey = limitKey;
        }
        long max = limiter.max();
        long ttl = limiter.ttl();
        TimeUnit timeUnit = limiter.timeUnit();
        return this.rateLimiterClient.allow((String)rateKey, max, ttl, timeUnit, () -> ((ProceedingJoinPoint)point).proceed());
    }

    private String evalLimitParam(ProceedingJoinPoint point, String limitParam) {
        MethodSignature ms = (MethodSignature)point.getSignature();
        Method method = ms.getMethod();
        Object[] args = point.getArgs();
        Object target = point.getTarget();
        Class<?> targetClass = target.getClass();
        EvaluationContext context = this.evaluator.createContext(method, args, target, targetClass, (BeanFactory)this.applicationContext);
        AnnotatedElementKey elementKey = new AnnotatedElementKey((AnnotatedElement)method, targetClass);
        return this.evaluator.evalAsText(limitParam, elementKey, context);
    }

    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

