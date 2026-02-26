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

package com.kuma.boot.lock.support.redisson;

import com.kuma.boot.lock.enums.LockScopeEnum;
import com.kuma.boot.lock.enums.LockTypeEnums;
import com.kuma.boot.lock.support.AbstractLockSupport;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * RedissonLock
 * </p>
 */
public class RedissonLock extends AbstractLockSupport<RLock> {

    private final RedissonClient redissonClient;

    public RedissonLock(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    protected RLock getLock(LockTypeEnums type, String key) {
        return switch (type) {
            case LOCK -> redissonClient.getLock(key);
            case FAIR -> redissonClient.getFairLock(key);
            case READ -> redissonClient.getReadWriteLock(key).readLock();
            case WRITE -> redissonClient.getReadWriteLock(key).writeLock();
        };
    }

    @Override
    protected boolean tryLock(RLock lock, long leaseTime, long waitTime, TimeUnit unit) throws Exception {
        return lock.tryLock(waitTime, leaseTime, unit);
    }

    @Override
    protected void lock(RLock lock, long leaseTime, TimeUnit unit) {
        lock.lock(leaseTime, unit);
    }

    @Override
    protected boolean tryLockAsync(RLock lock, long leaseTime, long waitTime, TimeUnit unit) throws Exception {
        return lock.tryLockAsync(waitTime, leaseTime, unit).get();
    }

    @Override
    protected void lockAsync(RLock lock, long leaseTime, TimeUnit unit) throws Exception {
        lock.lockAsync(leaseTime, unit).get();
    }

    @Override
    protected boolean isLocked(RLock lock) {
        return lock.isLocked() && lock.isHeldByCurrentThread();
    }

    @Override
    public LockScopeEnum scope() {
        return LockScopeEnum.DISTRIBUTED_LOCK;
    }

    @Override
    protected boolean supportAsync() {
        return true;
    }

    @Override
    protected boolean unlock(String key, RLock lock) {
        lock.unlock();
        return !isLocked(lock);
    }
}
