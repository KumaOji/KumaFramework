/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.ratelimit.ratelimitredis;

import com.kuma.boot.common.model.CharPool;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.core.utils.spel.ExpressionEvaluator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * redis 限流
 *
 * @author kuma
 * @version 2023.07
 * @see ApplicationContextAware
 * @since 2023-07-11 10:41:34
 */
@Aspect
public class RedisRateLimiterAspect implements ApplicationContextAware {

    /**
     * 表达式处理
     */
    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();
    /**
     * redis 限流服务
     */
    private final RedisRateLimiterClient rateLimiterClient;
    /**
     * 应用程序上下文
     */
    private ApplicationContext applicationContext;

    /**
     * redis速率限制器方面
     *
     * @param rateLimiterClient 速率限制器客户端
     * @since 2023-07-11 10:41:34
     */
    public RedisRateLimiterAspect(RedisRateLimiterClient rateLimiterClient) {
        this.rateLimiterClient = rateLimiterClient;
    }

    /**
     * AOP 环切 注解 @RateLimiter
     *
     * @param point   点
     * @param limiter 限制器
     * @return {@link Object }
     * @since 2023-07-11 10:41:35
     */
    @Around("@annotation(limiter)")
    public Object aroundRateLimiter(ProceedingJoinPoint point, com.kuma.boot.ratelimit.ratelimitredis.RateLimiter limiter) throws Throwable {
        String limitKey = limiter.value();
        Assert.hasText(limitKey, "@RateLimiter value must have length; it must not be null or empty");
        // el 表达式
        String limitParam = limiter.param();
        // 表达式不为空
        String rateKey;
        if (StringUtils.isNotBlank(limitParam)) {
            String evalAsText = evalLimitParam(point, limitParam);
            rateKey = limitKey + CharPool.COLON + evalAsText;
        } else {
            rateKey = limitKey;
        }
        long max = limiter.max();
        long ttl = limiter.ttl();
        TimeUnit timeUnit = limiter.timeUnit();
        return rateLimiterClient.allow(rateKey, max, ttl, timeUnit, point::proceed);
    }

    /**
     * 计算参数表达式
     *
     * @param point      ProceedingJoinPoint
     * @param limitParam limitParam
     * @return {@link String }
     * @since 2023-07-11 10:41:35
     */
    private String evalLimitParam(ProceedingJoinPoint point, String limitParam) {
        MethodSignature ms = (MethodSignature) point.getSignature();
        Method method = ms.getMethod();
        Object[] args = point.getArgs();
        Object target = point.getTarget();
        Class<?> targetClass = target.getClass();
        EvaluationContext context = evaluator.createContext(method, args, target, targetClass, applicationContext);
        AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
        return evaluator.evalAsText(limitParam, elementKey, context);
    }

    /**
     * 设置应用程序上下文
     *
     * @param applicationContext 应用程序上下文
     * @since 2023-07-11 10:41:35
     */
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
