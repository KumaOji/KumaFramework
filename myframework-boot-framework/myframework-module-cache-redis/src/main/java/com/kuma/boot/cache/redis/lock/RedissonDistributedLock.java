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

package com.kuma.boot.cache.redis.lock;

import com.kuma.boot.lock.support.DistributedLock;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * 基于 Redisson 的分布式锁实现
 *
 * @author kuma
 */
public class RedissonDistributedLock implements DistributedLock {

    private final RedissonClient redissonClient;
    private final ThreadLocal<RLock> currentLock = new ThreadLocal<>();

    public RedissonDistributedLock(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public boolean tryLock(String key, long time, TimeUnit unit) {
        RLock lock = redissonClient.getLock(key);
        try {
            boolean acquired = lock.tryLock(0, time, unit);
            if (acquired) {
                currentLock.set(lock);
            }
            return acquired;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void unlock() {
        RLock lock = currentLock.get();
        if (lock != null) {
            try {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            } finally {
                currentLock.remove();
            }
        }
    }
}
