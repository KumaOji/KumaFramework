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

package com.kuma.boot.common.support.syncwaiting.concept;

import com.kuma.boot.common.support.syncwaiting.caller.SyncCaller;
import com.kuma.boot.common.support.syncwaiting.configuration.SyncWaitingConfiguration;
import com.kuma.boot.common.support.syncwaiting.container.SyncWaiterContainer;
import com.kuma.boot.common.support.syncwaiting.recycler.SyncWaiterRecycler;

/**
 * {@link SyncWaitingConcept} 的抽象类。
 */
public abstract class AbstractSyncWaitingConcept implements SyncWaitingConcept {

    protected final SyncWaiterContainer container;

    protected final SyncWaiterRecycler recycler;

    public AbstractSyncWaitingConcept(SyncWaiterContainer container, SyncWaiterRecycler recycler) {
        this.container = container;
        this.recycler = recycler;
    }

    /**
     * 阻塞等待。 每个请求都会加锁， 通过 key 获得当前等待的 {@link SyncWaiter}， 如果已经存在对应的 {@link SyncWaiter}，
     * 则当前线程排队等待不进行业务逻辑调用， 直到上一个对应 key 的请求线程被唤醒或超时后再重试； 如果不存在对应的 {@link SyncWaiter}，
     * 重新使用一个 {@link SyncWaiter}， 调用业务接口 {@link SyncCaller}， 如果调用过程中发生异常， 回收
     * {@link SyncCaller} 并抛出异常， 如果调用成功执行阻塞等待， 成功唤醒则回收 {@link SyncCaller} 并返回获得的值， 否则回收
     * {@link SyncCaller} 并直接唤醒，同时抛出异常， 最后解锁。
     * @param key 标识
     * @param caller 业务调用回调
     * @param configuration 等待配置 {@link SyncWaitingConfiguration}
     * @param <T> 值类型
     * @return 值
     */
    @Override
    public <T> T waitSync(Object key, SyncCaller caller, SyncWaitingConfiguration configuration) {
        lock();
        try {
            SyncWaiter exist = findWaitingSyncWaiter(key);
            if (exist == null) {
                SyncWaiter waiter = reuseSyncWaiter(key);
                if (waiter == null) {
                    throw new NullPointerException("No SyncWaiter to reuse");
                }
                try {
                    caller.call(key);
                } catch (Throwable e) {
                    recycleSyncWaiter(key);
                    throw e;
                }
                try {
                    waiter.performWait(configuration.getWaitingTime());
                    T value = waiter.value();
                    recycleSyncWaiter(key);
                    return value;
                } catch (Throwable e) {
                    recycleSyncWaiter(key);
                    waiter.performNotify();
                    throw e;
                }
            } else {
                exist.performWait(configuration.getQueuingTime());
                return waitSync(key, caller, configuration);
            }
        } finally {
            unlock();
        }
    }

    /**
     * 异步唤醒。 加锁， 获得正在等待中的 {@link SyncWaiter}， 如果不为 null， 则设置值并唤醒对应线程， 解锁。
     * @param key 标识
     * @param value 值
     */
    @Override
    public void notifyAsync(Object key, Object value) {
        lock();
        try {
            SyncWaiter waiter = findWaitingSyncWaiter(key);
            if (waiter != null) {
                waiter.value(value);
                waiter.performNotify();
            }
        } finally {
            unlock();
        }
    }

    /**
     * 是否等待。 加锁， 返会是否存在等待中的 {@link SyncWaiter}， 解锁。
     * @param key 标识
     * @return 如果存在等待中的 {@link SyncWaiter} 则返回 true
     */
    @Override
    public boolean isWaiting(Object key) {
        lock();
        try {
            return isSyncWaiterWaiting(key);
        } finally {
            unlock();
        }
    }

    /**
     * 是否存在等待中的 {@link SyncWaiter}。
     * @param key 标识
     * @return 如果存在等待中的 {@link SyncWaiter} 则返回 true
     */
    public boolean isSyncWaiterWaiting(Object key) {
        return container.contains(key);
    }

    /**
     * 根据 key 获得正在等待中的 {@link SyncWaiter}， 没有则返回 null。
     * @param key 标识
     * @return 正在等待中的 {@link SyncWaiter} 或 null
     */
    public SyncWaiter findWaitingSyncWaiter(Object key) {
        return container.find(key);
    }

    /**
     * 重新使用一个 {@link SyncWaiter}。
     * @param key 标识
     * @return 重新使用的 {@link SyncWaiter}
     */
    public SyncWaiter reuseSyncWaiter(Object key) {
        SyncWaiter waiter = reuseOrCreate();
        if (waiter == null) {
            throw new NullPointerException("No SyncWaiter reuse or create");
        }
        waiter.key(key);
        container.add(waiter);
        return waiter;
    }

    /**
     * 重新使用或创建一个 {@link SyncWaiter}。
     * @return 重新使用或创建的 {@link SyncWaiter}
     */
    public SyncWaiter reuseOrCreate() {
        SyncWaiter waiter = recycler.reuse();
        if (waiter == null) {
            return createSyncWaiter();
        } else {
            return waiter;
        }
    }

    /**
     * 加锁。
     */
    public abstract void lock();

    /**
     * 解锁。
     */
    public abstract void unlock();

    /**
     * 创建一个 {@link SyncWaiter}。
     * @return 新建的 {@link SyncWaiter}
     */
    public abstract SyncWaiter createSyncWaiter();

    /**
     * 回收 {@link SyncWaiter}。
     * @param key 标识
     */
    public void recycleSyncWaiter(Object key) {
        resetAndRecycle(container.remove(key));
    }

    /**
     * 重置并回收 {@link SyncWaiter}。
     * @param waiter 需要重置和回收的 {@link SyncWaiter}。
     */
    public void resetAndRecycle(SyncWaiter waiter) {
        if (waiter == null) {
            return;
        }
        waiter.key(null);
        waiter.value(null);
        recycler.recycle(waiter);
    }

    /**
     * {@link SyncWaiter} 的抽象类。
     */
    public abstract static class AbstractSyncWaiter implements SyncWaiter {

        /**
         * 标识
         */
        private Object key;

        /**
         * 值
         */
        private Object value;

        /**
         * 获得标识。
         * @return 标识
         */
        @Override
        public Object key() {
            return key;
        }

        /**
         * 设置标识。
         * @param key 标识
         */
        @Override
        public void key(Object key) {
            this.key = key;
        }

        /**
         * 获得值。
         * @param <T> 值类型
         * @return 值
         */
        @SuppressWarnings("unchecked")
        @Override
        public <T> T value() {
            return (T) value;
        }

        /**
         * 设置值。
         * @param value 值
         */
        @Override
        public void value(Object value) {
            this.value = value;
        }
    }
}
