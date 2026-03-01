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

import com.kuma.boot.lock.enums.LockScopeEnum;
import com.kuma.boot.lock.enums.LockTypeEnums;
import com.kuma.boot.lock.exception.LockException;
import com.kuma.boot.lock.exception.UnSupportLockException;
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
    public boolean tryLock(LockTypeEnums type, String key, long leaseTime, long waitTime, TimeUnit unit, boolean async) {
        if (async) {
            throw new UnSupportLockException("Async lock of RedissonDistributedLock is not supported");
        }
        RLock lock = getLock(type, key);
        try {
            long wt = waitTime < 0 ? 0 : waitTime;
            long lt = leaseTime < 0 ? -1 : leaseTime;
            boolean acquired = lock.tryLock(wt, lt, unit);
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
    public void lock(LockTypeEnums type, String key, long leaseTime, TimeUnit unit, boolean async) {
        if (async) {
            throw new UnSupportLockException("Async lock of RedissonDistributedLock is not supported");
        }
        RLock lock = getLock(type, key);
        try {
            if (leaseTime < 0 || unit == null) {
                lock.lock();
            } else {
                lock.lock(leaseTime, unit);
            }
            currentLock.set(lock);
        } catch (Exception e) {
            currentLock.remove();
            throw new LockException(e);
        }
    }

    private RLock getLock(LockTypeEnums type, String key) {
        return switch (type) {
            case LOCK -> redissonClient.getLock(key);
            case FAIR -> redissonClient.getFairLock(key);
            case READ -> redissonClient.getReadWriteLock(key).readLock();
            case WRITE -> redissonClient.getReadWriteLock(key).writeLock();
            default -> redissonClient.getLock(key);
        };
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

    @Override
    public LockScopeEnum scope() {
        return LockScopeEnum.DISTRIBUTED_LOCK;
    }
}
