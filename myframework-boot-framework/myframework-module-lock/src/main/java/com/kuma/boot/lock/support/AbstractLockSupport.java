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

package com.kuma.boot.lock.support;

import com.kuma.boot.common.support.tuple.impl.Pair;
import com.kuma.boot.lock.enums.LockTypeEnums;
import com.kuma.boot.lock.exception.LockException;
import com.kuma.boot.lock.exception.UnSupportLockException;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * AbstractLock
 * </p>
 *
 * @param <T> the type parameter
 */
public abstract class AbstractLockSupport<T> implements DistributedLock {
    /**
     * The Thread local.
     */
    protected final ThreadLocal<Pair<String, T>> threadLocal = new InheritableThreadLocal<>();

    @Override
    public boolean tryLock(LockTypeEnums type, String key, long leaseTime, long waitTime, TimeUnit unit, boolean async) {
        T lock = getLock(type, key);
        try {
            boolean isLocked = supportAsync() && async
                    ? tryLockAsync(lock, leaseTime, waitTime, unit)
                    : tryLock(lock, leaseTime, waitTime, unit);
            if (isLocked) {
                threadLocal.set(Pair.of(key, lock));
            }
            return isLocked;
        } catch (Exception e) {
            threadLocal.remove();
            throw new LockException(e);
        }
    }

    @Override
    public void lock(LockTypeEnums type, String key, long leaseTime, TimeUnit unit, boolean async) {
        T lock = getLock(type, key);
        try {
            if (supportAsync() && async) {
                lockAsync(lock, leaseTime, unit);
            } else {
                lock(lock, leaseTime, unit);
            }
            threadLocal.set(Pair.of(key, lock));
        } catch (Exception e) {
            threadLocal.remove();
            throw new LockException(e);
        }
    }

    @Override
    public void unlock() {
        Pair<String, T> pair = threadLocal.get();
        if (pair != null) {
            String key = pair.getValueOne();
            T lock = pair.getValueTwo();
            if (isLocked(lock) && unlock(key, lock)) {
                threadLocal.remove();
            }
        }
    }

    /**
     * Gets lock.
     *
     * @param type the type
     * @param key  the key
     * @return the lock
     */
    protected abstract T getLock(LockTypeEnums type, String key);

    /**
     * Unlock.
     *
     * @param key  the key
     * @param lock the lock
     * @return the boolean
     */
    protected abstract boolean unlock(String key, T lock);

    /**
     * Try lock async boolean.
     *
     * @param lock      the lock
     * @param leaseTime the lease time
     * @param waitTime  the wait time
     * @return the boolean
     * @throws Exception the exception
     */
    protected boolean tryLockAsync(T lock, long leaseTime, long waitTime, TimeUnit unit) throws Exception {
        throw new UnSupportLockException("Async lock of " + this.getClass().getSimpleName() + " isn't support");
    }

    /**
     * Try lock boolean.
     *
     * @param lock      the lock
     * @param leaseTime the lease time
     * @param waitTime  the wait time
     * @return the boolean
     * @throws Exception the exception
     */
    protected abstract boolean tryLock(T lock, long leaseTime, long waitTime, TimeUnit unit) throws Exception;

    /**
     * Lock async.
     *
     * @param lock the lock
     * @throws Exception the exception
     */
    protected void lockAsync(T lock, long leaseTime, TimeUnit unit) throws Exception {
        throw new UnSupportLockException("Async lock of " + this.getClass().getSimpleName() + " isn't support");
    }

    /**
     * Lock.
     *
     * @param lock the lock
     * @throws Exception the exception
     */
    protected abstract void lock(T lock, long leaseTime, TimeUnit unit) throws Exception;

    /**
     * Is locked boolean.
     *
     * @param lock the lock
     * @return the boolean
     */
    protected abstract boolean isLocked(T lock);

    /**
     * Support async boolean.
     *
     * @return the boolean
     */
    protected boolean supportAsync() {
        return false;
    }
}
