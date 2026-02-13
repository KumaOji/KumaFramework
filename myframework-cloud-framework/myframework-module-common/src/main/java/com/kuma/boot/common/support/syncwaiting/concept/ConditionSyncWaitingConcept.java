/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.syncwaiting.concept;

import com.kuma.boot.common.support.syncwaiting.concept.AbstractSyncWaitingConcept;
import com.kuma.boot.common.support.syncwaiting.concept.SyncWaiter;
import com.kuma.boot.common.support.syncwaiting.container.MapSyncWaiterContainer;
import com.kuma.boot.common.support.syncwaiting.container.SyncWaiterContainer;
import com.kuma.boot.common.support.syncwaiting.exception.SyncWaitingTimeoutException;
import com.kuma.boot.common.support.syncwaiting.recycler.DisposableSyncWaiterRecycler;
import com.kuma.boot.common.support.syncwaiting.recycler.SyncWaiterRecycler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionSyncWaitingConcept
extends AbstractSyncWaitingConcept {
    protected final Lock lock;

    public ConditionSyncWaitingConcept() {
        this(new MapSyncWaiterContainer(), new DisposableSyncWaiterRecycler(), new ReentrantLock());
    }

    protected ConditionSyncWaitingConcept(SyncWaiterContainer container, SyncWaiterRecycler recycler, Lock lock) {
        super(container, recycler);
        this.lock = lock;
    }

    @Override
    public void lock() {
        try {
            this.lock.lockInterruptibly();
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unlock() {
        this.lock.unlock();
    }

    @Override
    public SyncWaiter createSyncWaiter() {
        return new ConditionSyncWaiter(this.lock.newCondition());
    }

    public static class ConditionSyncWaiter
    extends AbstractSyncWaitingConcept.AbstractSyncWaiter {
        private final Condition condition;

        public ConditionSyncWaiter(Condition condition) {
            this.condition = condition;
        }

        public Condition getCondition() {
            return this.condition;
        }

        @Override
        public void performWait(long time) {
            block7: {
                if (time > 0L) {
                    try {
                        if (!this.condition.await(time, TimeUnit.MILLISECONDS)) {
                            throw new SyncWaitingTimeoutException(time + "ms");
                        }
                        break block7;
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    catch (SyncWaitingTimeoutException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    this.condition.await();
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public void performNotify() {
            this.condition.signalAll();
        }
    }

    public static class Builder {
        private Lock lock;
        private SyncWaiterContainer container;
        private SyncWaiterRecycler recycler;

        public Builder lock(Lock lock) {
            this.lock = lock;
            return this;
        }

        public Builder container(SyncWaiterContainer container) {
            this.container = container;
            return this;
        }

        public Builder recycler(SyncWaiterRecycler recycler) {
            this.recycler = recycler;
            return this;
        }

        public ConditionSyncWaitingConcept build() {
            if (this.lock == null) {
                this.lock = new ReentrantLock();
            }
            if (this.container == null) {
                this.container = new MapSyncWaiterContainer();
            }
            if (this.recycler == null) {
                this.recycler = new DisposableSyncWaiterRecycler();
            }
            return new ConditionSyncWaitingConcept(this.container, this.recycler, this.lock);
        }
    }
}

