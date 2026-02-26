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

package com.kuma.boot.ratelimit.ratelimitprovider;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 速率限制方面处理程序
 *
 * @author kuma
 * @version 2022.09
 * @since 2022-10-26 08:56:25
 */
@Aspect
@Order(0)
public class RateLimitAspectHandler {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitAspectHandler.class);

    private final RateLimiterService rateLimiterService;
    private final RScript rScript;

    public RateLimitAspectHandler(RedissonClient client, RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
        this.rScript = client.getScript();
    }

    @Around(value = "@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, com.kuma.boot.ratelimit.ratelimitprovider.RateLimit rateLimit) throws Throwable {
        RateLimiterInfo limiterInfo = rateLimiterService.getRateLimiterInfo(joinPoint, rateLimit);

        List<Object> keys = new ArrayList<>();
        keys.add(limiterInfo.getKey());
        keys.add(limiterInfo.getRate());
        keys.add(limiterInfo.getRateInterval());
        List<Long> results =
                rScript.eval(RScript.Mode.READ_WRITE, com.kuma.boot.ratelimit.ratelimitprovider.LuaScript.getRateLimiterScript(), RScript.ReturnType.LIST, keys);
        boolean allowed = results.get(0) == 0L;
        if (!allowed) {
            logger.info("Trigger current limiting,key:{}", limiterInfo.getKey());
            if (StringUtils.hasLength(rateLimit.fallbackFunction())) {
                return rateLimiterService.executeFunction(rateLimit.fallbackFunction(), joinPoint);
            }
            long ttl = results.get(1);
            throw new RateLimitException("Too Many Requests", ttl);
        }
        return joinPoint.proceed();
    }
}
