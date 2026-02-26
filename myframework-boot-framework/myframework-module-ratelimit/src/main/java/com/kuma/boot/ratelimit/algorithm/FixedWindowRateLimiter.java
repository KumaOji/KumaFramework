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

import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redisson的实现固定窗口相当简单。在每个窗口期内，我们可以通过incrementAndGet操作来统计请求的数量。一旦窗口期结束，我们可以利用Redis的键过期功能来自动重置计数。
 *
 *
 * 固定窗口算法的优点是实现简单，占用空间小，但是它存在临界问题，由于窗口的切换是瞬间完成的，因此请求的处理并不平滑，可能会在窗口切换的瞬间出现流量的剧烈波动。
 *
 * 比如这个例子，假如在00:02，突然有大量请求过来，但是我们这时候计数重置了，那么就没法限制突发的这些流量。
 */
public class FixedWindowRateLimiter {
    public static final String KEY = "fixedWindowRateLimiter:";
    private RedissonClient redissonClient;
    /**
     * 请求限制数量
     */
    private Long limit;
    /**
     * 窗口大小（单位：S）
     */
    private Long windowSize;

    public FixedWindowRateLimiter(Long limit, Long windowSize) {
        this.limit = limit;
        this.windowSize = windowSize;
    }

    /**
     * 固定窗口限流
     */
    public boolean triggerLimit(String path) {
        // 加分布式锁，防止并发情况下窗口初始化时间不一致问题
        RLock rLock = redissonClient.getLock(KEY + "LOCK:" + path);
        try {
            rLock.lock(100, TimeUnit.MILLISECONDS);
            String redisKey = KEY + path;
            RAtomicLong counter = redissonClient.getAtomicLong(redisKey);
            // 计数
            long count = counter.incrementAndGet();
            // 如果为1的话，就说明窗口刚初始化
            if (count == 1) {
                // 直接设置过期时间，作为窗口
                counter.expire(Duration.ofSeconds(windowSize));
            }
            // 触发限流
            if (count > limit) {
                // 触发限流的不记在请求数量中
                counter.decrementAndGet();
                return true;
            }
            return false;
        } finally {
            rLock.unlock();
        }
    }
}
