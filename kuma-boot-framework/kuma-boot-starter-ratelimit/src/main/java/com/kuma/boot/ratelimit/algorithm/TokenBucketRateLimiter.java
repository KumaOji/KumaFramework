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

package com.kuma.boot.ratelimit.algorithm;

import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

import java.time.Duration;

/**
 * 令牌桶算法是对漏桶算法的一种改进。
 *
 * 它的主要思想是：系统以一种固定的速率向桶中添加令牌，每个请求在发送前都需要从桶中取出一个令牌，只有取到令牌的请求才被通过。因此，令牌桶算法允许请求以任意速率发送，只要桶中有足够的令牌。
 *
 * 我们继续看怎么实现，首先是要发放令牌，要固定速率，那我们又得开个线程，定时往桶里投令牌，然后……
 *
 * ——然后Redission提供了令牌桶算法的实现，舒不舒服？
 */
public class TokenBucketRateLimiter {

    public static final String KEY = "TokenBucketRateLimiter:";
    private RedissonClient redissonClient;
    /**
     * 阈值
     */
    private Long limit;
    /**
     * 添加令牌的速率，单位：个/秒
     */
    private Long tokenRate;

    public TokenBucketRateLimiter(Long limit, Long tokenRate) {
        this.limit = limit;
        this.tokenRate = tokenRate;
    }

    /**
     * 限流算法
     */
    public boolean triggerLimit(String path) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(KEY + path);
        // 初始化，设置速率模式，速率，间隔，间隔单位
        rateLimiter.trySetRate(RateType.OVERALL, limit, Duration.ofSeconds(tokenRate));
        // 获取令牌
        return rateLimiter.tryAcquire();
    }
}
