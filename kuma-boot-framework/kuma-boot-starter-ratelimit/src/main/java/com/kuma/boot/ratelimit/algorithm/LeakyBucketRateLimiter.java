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

import com.kuma.boot.common.utils.log.LogUtils;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/**
 * 计数器类的限流，体现的是一个“戛然而止”，超过限制，立马决绝，但是有时候，我们可能只是希望请求平滑一些，追求的是“波澜不惊”，这时候就可以考虑使用其它的限流算法。
 *
 * 漏桶算法（Leaky Bucket），名副其实，就是请求就像水一样以任意速度注入漏桶，而桶会按照固定的速率将水漏掉。
 *
 * 当进水速率大于出水速率的时候，漏桶会变满，此时新进入的请求将会被丢弃。
 * 漏桶算法的两大作用是网络流量整形（Traffic Shaping）和速度限制（Rate Limiting）。
 *
 * 在滑动窗口限流算法里我们用到了RScoredSortedSet，非常好用对不对，这里也可以用这个结构，直接使用ZREMRANGEBYSCORE命令来删除旧的请求。
 * 进水就不用多说了，请求进来，判断桶有没有满，满了就拒绝，没满就往桶里丢请求。
 * 那么出水怎么办呢？得保证稳定速率出水，可以用一个定时任务，来定时去删除旧的请求。
 *
 * 在代码实现里，我们用了RSet来存储path，这样一来，一个定时任务，就可以搞定所有path对应的桶的出水，而不用每个桶都创建一个一个定时任务。
 *
 * 这里我直接用ScheduledExecutorService启动了一个定时任务，1s跑一次，当然集群环境下，每台机器都跑一个定时任务，对性能是极大的浪费，而且不好管理，我们可以用分布式定时任务，比如xxl-job去执行leakWater。
 *
 * 漏桶算法能够有效防止网络拥塞，实现也比较简单。
 *
 * 但是，因为漏桶的出水速率是固定的，假如突然来了大量的请求，那么只能丢弃超量的请求，即使下游能处理更大的流量，没法充分利用系统资源。
 *
 */
public class LeakyBucketRateLimiter {
    private RedissonClient redissonClient;
    private static final String KEY_PREFIX = "LeakyBucket:";

    /**
     * 桶的大小
     */
    private Long bucketSize;
    /**
     * 漏水速率，单位:个/秒
     */
    private Long leakRate;

    public LeakyBucketRateLimiter(Long bucketSize, Long leakRate) {
        this.bucketSize = bucketSize;
        this.leakRate = leakRate;
        // 这里启动一个定时任务，每s执行一次
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(this::leakWater, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * 漏水
     */
    public void leakWater() {
        RSet<String> pathSet = redissonClient.getSet(KEY_PREFIX + ":pathSet");
        // 遍历所有path,删除旧请求
        for (String path : pathSet) {
            String redisKey = KEY_PREFIX + path;
            RScoredSortedSet<Long> bucket = redissonClient.getScoredSortedSet(KEY_PREFIX + path);
            // 获取当前时间
            long now = System.currentTimeMillis();
            // 删除旧的请求
            bucket.removeRangeByScore(0, true, now - 1000 * leakRate, true);
        }
    }

    /**
     * 限流
     */
    public boolean triggerLimit(String path) {
        // 加锁，防止并发初始化问题
        RLock rLock = redissonClient.getLock(KEY_PREFIX + "LOCK:" + path);
        try {
            rLock.lock(100, TimeUnit.MILLISECONDS);
            String redisKey = KEY_PREFIX + path;
            RScoredSortedSet<Long> bucket = redissonClient.getScoredSortedSet(redisKey);
            // 这里用一个set，来存储所有path
            RSet<String> pathSet = redissonClient.getSet(KEY_PREFIX + ":pathSet");
            pathSet.add(path);
            // 获取当前时间
            long now = System.currentTimeMillis();
            // 检查桶是否已满
            if (bucket.size() < bucketSize) {
                // 桶未满，添加一个元素到桶中
                bucket.add(now, now);
                return false;
            }
            // 桶已满，触发限流
            LogUtils.info("[triggerLimit] path:" + path + " bucket size:" + bucket.size());
            return true;
        } finally {
            rLock.unlock();
        }
    }
}
