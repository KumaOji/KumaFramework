/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

import com.kuma.boot.common.support.syncwaiting.container.MapSyncWaiterContainer;
import com.kuma.boot.common.support.syncwaiting.container.SyncWaiterContainer;
import com.kuma.boot.common.support.syncwaiting.exception.SyncWaitingTimeoutException;
import com.kuma.boot.common.support.syncwaiting.recycler.DisposableSyncWaiterRecycler;
import com.kuma.boot.common.support.syncwaiting.recycler.SyncWaiterRecycler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于 {@link Condition } 实现的 {@link SyncWaitingConcept}。
 */
public class ConditionSyncWaitingConcept extends AbstractSyncWaitingConcept {

    /**
     * 锁
     */
    protected final Lock lock;

    public ConditionSyncWaitingConcept() {
        this(new MapSyncWaiterContainer(), new DisposableSyncWaiterRecycler(), new ReentrantLock());
    }

    protected ConditionSyncWaitingConcept(
            SyncWaiterContainer container, SyncWaiterRecycler recycler, Lock lock ) {
        super(container, recycler);
        this.lock = lock;
    }

    /**
     * 加锁。
     */
    @Override
    public void lock() {
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解锁。
     */
    @Override
    public void unlock() {
        lock.unlock();
    }

    /**
     * 创建一个 {@link ConditionSyncWaiter}。
     *
     * @return 新建的 {@link ConditionSyncWaiter}
     */
    @Override
    public SyncWaiter createSyncWaiter() {
        return new ConditionSyncWaiter(lock.newCondition());
    }

    /**
     * 基于 {@link Condition } 实现的 {@link SyncWaiter}。
     */
    public static class ConditionSyncWaiter extends AbstractSyncWaiter {

        private final Condition condition;

        public ConditionSyncWaiter( Condition condition ) {
            this.condition = condition;
        }

        public Condition getCondition() {
            return condition;
        }

        /**
         * 如果超时时间大于 0 则调用 {@link Condition#await(long, TimeUnit)}， 否则调用 {@link Condition#await()}。
         *
         * @param time 等待超时时间
         */
        @Override
        public void performWait( long time ) {
            if (time > 0) {
                try {
                    if (!condition.await(time, TimeUnit.MILLISECONDS)) {
                        throw new SyncWaitingTimeoutException(time + "ms");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (SyncWaitingTimeoutException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        /**
         * 调用 {@link Condition#signalAll()}。
         */
        @Override
        public void performNotify() {
            condition.signalAll();
        }
    }

    /**
     * Builder
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-17 10:30:45
     */
    public static class Builder {

        private Lock lock;

        private SyncWaiterContainer container;

        private SyncWaiterRecycler recycler;

        public Builder lock( Lock lock ) {
            this.lock = lock;
            return this;
        }

        public Builder container( SyncWaiterContainer container ) {
            this.container = container;
            return this;
        }

        public Builder recycler( SyncWaiterRecycler recycler ) {
            this.recycler = recycler;
            return this;
        }

        public ConditionSyncWaitingConcept build() {
            if (lock == null) {
                lock = new ReentrantLock();
            }
            if (container == null) {
                container = new MapSyncWaiterContainer();
            }
            if (recycler == null) {
                recycler = new DisposableSyncWaiterRecycler();
            }
            return new ConditionSyncWaitingConcept(container, recycler, lock);
        }
    }
}
