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

package com.kuma.boot.lock.support.local;

import com.kuma.boot.lock.enums.LockScopeEnum;
import com.kuma.boot.lock.enums.LockTypeEnums;
import com.kuma.boot.lock.support.AbstractLockSupport;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>
 * LocalLock
 * </p>
 *
 *
 */
public class LocalLock extends AbstractLockSupport<Lock> {

    private static final Map<String, Lock> CACHE_LOCK = new ConcurrentHashMap<>();

    @Override
    protected Lock getLock(LockTypeEnums type, String key) {
        return CACHE_LOCK.computeIfAbsent(key, s -> switch (type) {
            case LOCK -> new ReentrantLock();
            case FAIR -> new ReentrantLock(true);
            case READ -> new ReentrantReadWriteLock().readLock();
            case WRITE -> new ReentrantReadWriteLock().writeLock();
        });
    }

    @Override
    protected boolean tryLock(Lock lock, long leaseTime, long waitTime, TimeUnit unit) throws Exception {
        return lock.tryLock(waitTime, unit);
    }

    @Override
    protected void lock(Lock lock, long leaseTime, TimeUnit unit) {
        lock.lock();
    }

    @Override
    protected boolean unlock(String key, Lock lock) {
        lock.unlock();
        return !isLocked(lock);
    }

    @Override
    protected boolean isLocked(Lock lock) {
        if (lock instanceof ReentrantLock reentrantLock) {
            return reentrantLock.isLocked() && reentrantLock.isHeldByCurrentThread();
        } else if (lock instanceof ReentrantReadWriteLock.WriteLock writeLock) {
            return writeLock.getHoldCount() != 0 && writeLock.isHeldByCurrentThread();
        }
        return false;
    }

    @Override
    public LockScopeEnum scope() {
        return LockScopeEnum.STANDALONE_LOCK;
    }
}
