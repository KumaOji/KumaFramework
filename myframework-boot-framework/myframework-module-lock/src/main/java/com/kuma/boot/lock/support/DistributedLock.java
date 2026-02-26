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

import com.kuma.boot.lock.enums.LockScopeEnum;
import com.kuma.boot.lock.enums.LockTypeEnums;
import org.springframework.core.Ordered;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 *
 * @author kuma
 * @version 2023.04
 * @since 2023-05-09 10:40:08
 */
public interface DistributedLock extends Ordered {

    /**
     * 尝试获取锁，如果锁不可用则等待最多waitTime时间后放弃
     *
     * @param type      锁类型
     * @param key       锁的key
     * @param waitTime  获取锁的最大尝试时间(单位 {@code unit})
     * @param leaseTime 加锁的时间，超过这个时间后锁便自动解锁； 如果leaseTime为-1，则保持锁定直到显式解锁
     * @param unit      {@code waitTime} 和 {@code leaseTime} 参数的时间单位
     * @param async     是否异步(只有使用redison支持异步)
     * @return the boolean
     */
    boolean tryLock(LockTypeEnums type, String key, long leaseTime, long waitTime, TimeUnit unit, boolean async);

    default boolean tryLock(LockTypeEnums type, String key, long waitTime, long leaseTime, TimeUnit unit) {
        return this.tryLock(type, key, waitTime, leaseTime, unit, false);
    }

    default boolean tryLock(LockTypeEnums type, String key, long waitTime, TimeUnit unit) {
        return this.tryLock(type, key, -1, waitTime, unit, false);
    }

    default boolean tryLock(String key, long waitTime, TimeUnit unit) {
        return this.tryLock(LockTypeEnums.LOCK, key, -1, waitTime, unit, false);
    }

    /**
     * 加锁.
     *
     * @param type      the 锁类型
     * @param key       锁key
     * @param leaseTime 加锁的时间，超过这个时间后锁便自动解锁； 如果leaseTime为-1，则保持锁定直到显式解锁
     * @param unit      参数的时间单位
     * @param async     是否异步(只有使用redison支持异步)
     */
    void lock(LockTypeEnums type, String key, long leaseTime, TimeUnit unit, boolean async);

    default void lock(LockTypeEnums type, String key, long leaseTime, TimeUnit unit) {
        this.lock(type, key, leaseTime, unit, false);
    }

    default void lock(LockTypeEnums type, String key) {
        this.lock(type, key, -1, null);
    }

    default void lock(String key) {
        this.lock(LockTypeEnums.LOCK, key, -1, null, false);
    }

    /**
     * Unlock.
     */
    void unlock();

    /**
     * Scope lock scope.
     *
     * @return the lock scope
     */
    LockScopeEnum scope();

    @Override
    default int getOrder() {
        return 0;
    }
}
