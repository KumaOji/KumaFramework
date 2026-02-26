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

package com.kuma.boot.ratelimit.ratelimitenhance.aspect;

import com.kuma.boot.ratelimit.ratelimitenhance.annotation.TokenBucketLimit;
import com.kuma.boot.ratelimit.ratelimitenhance.exception.EnhanceRedisLimitException;
import com.kuma.boot.ratelimit.ratelimitenhance.helper.RedisLimitHelper;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** 令牌桶限流切面 */
@Order(0)
@Aspect
@Component
public class TokenBucketLimitAspect {

    private static final String DOT = ".";

    private final RedisLimitHelper redisLimitHelper;

    public TokenBucketLimitAspect(RedisLimitHelper redisLimitHelper) {
        this.redisLimitHelper = redisLimitHelper;
    }

    /** 切点 */
    @Pointcut("@annotation(com.kuma.boot.ratelimit.ratelimitenhance.annotation.TokenBucketLimit)")
    public void aspect() {}

    @Around("aspect() && @annotation(limit)")
    public Object interceptor(ProceedingJoinPoint proceedingJoinPoint, TokenBucketLimit limit) throws Throwable {
        Object result;
        String limitKey;
        // 未配置限流key的情况下，以 [类名 + 方法名] 作为限流key
        if (StringUtils.isBlank(limit.limitKey())) {
            MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
            limitKey = signature.getDeclaringTypeName()
                    + DOT
                    + signature.getMethod().getName();
        } else {
            limitKey = limit.limitKey();
        }
        // 使用滑动时间窗口限流法
        Boolean pass = redisLimitHelper.tokenLimit(limitKey, limit.capacity(), limit.permits(), limit.rate());
        // 通过则放行目标方法
        if (pass) {
            result = proceedingJoinPoint.proceed();
        } else {
            // 这里避免并发情况下频繁打印异常堆栈消耗性能，这里抛出自定义异常！
            throw new EnhanceRedisLimitException("Congestion requested, please try again later.");
        }

        return result;
    }
}
