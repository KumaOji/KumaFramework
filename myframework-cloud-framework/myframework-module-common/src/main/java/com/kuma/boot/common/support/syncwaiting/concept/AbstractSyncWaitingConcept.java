/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.syncwaiting.concept;

import com.kuma.boot.common.support.syncwaiting.caller.SyncCaller;
import com.kuma.boot.common.support.syncwaiting.concept.SyncWaiter;
import com.kuma.boot.common.support.syncwaiting.concept.SyncWaitingConcept;
import com.kuma.boot.common.support.syncwaiting.configuration.SyncWaitingConfiguration;
import com.kuma.boot.common.support.syncwaiting.container.SyncWaiterContainer;
import com.kuma.boot.common.support.syncwaiting.recycler.SyncWaiterRecycler;

public abstract class AbstractSyncWaitingConcept
implements SyncWaitingConcept {
    protected final SyncWaiterContainer container;
    protected final SyncWaiterRecycler recycler;

    public AbstractSyncWaitingConcept(SyncWaiterContainer container, SyncWaiterRecycler recycler) {
        this.container = container;
        this.recycler = recycler;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T> T waitSync(Object key, SyncCaller caller, SyncWaitingConfiguration configuration) {
        this.lock();
        try {
            SyncWaiter exist = this.findWaitingSyncWaiter(key);
            if (exist == null) {
                SyncWaiter waiter = this.reuseSyncWaiter(key);
                if (waiter == null) {
                    throw new NullPointerException("No SyncWaiter to reuse");
                }
                try {
                    caller.call(key);
                } catch (Throwable e) {
                    this.recycleSyncWaiter(key);
                    throw e;
                }
                try {
                    waiter.performWait(configuration.getWaitingTime());
                    Object value = waiter.value();
                    this.recycleSyncWaiter(key);
                    return (T) value;
                } catch (Throwable e) {
                    this.recycleSyncWaiter(key);
                    waiter.performNotify();
                    throw e;
                }
            }
            exist.performWait(configuration.getQueuingTime());
        } finally {
            this.unlock();
        }
        return this.waitSync(key, caller, configuration);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void notifyAsync(Object key, Object value) {
        this.lock();
        try {
            SyncWaiter waiter = this.findWaitingSyncWaiter(key);
            if (waiter != null) {
                waiter.value(value);
                waiter.performNotify();
            }
        }
        finally {
            this.unlock();
        }
    }

    @Override
    public boolean isWaiting(Object key) {
        this.lock();
        try {
            boolean bl = this.isSyncWaiterWaiting(key);
            return bl;
        }
        finally {
            this.unlock();
        }
    }

    public boolean isSyncWaiterWaiting(Object key) {
        return this.container.contains(key);
    }

    public SyncWaiter findWaitingSyncWaiter(Object key) {
        return this.container.find(key);
    }

    public SyncWaiter reuseSyncWaiter(Object key) {
        SyncWaiter waiter = this.reuseOrCreate();
        if (waiter == null) {
            throw new NullPointerException("No SyncWaiter reuse or create");
        }
        waiter.key(key);
        this.container.add(waiter);
        return waiter;
    }

    public SyncWaiter reuseOrCreate() {
        SyncWaiter waiter = this.recycler.reuse();
        if (waiter == null) {
            return this.createSyncWaiter();
        }
        return waiter;
    }

    public abstract void lock();

    public abstract void unlock();

    public abstract SyncWaiter createSyncWaiter();

    public void recycleSyncWaiter(Object key) {
        this.resetAndRecycle(this.container.remove(key));
    }

    public void resetAndRecycle(SyncWaiter waiter) {
        if (waiter == null) {
            return;
        }
        waiter.key(null);
        waiter.value(null);
        this.recycler.recycle(waiter);
    }

    public static abstract class AbstractSyncWaiter
    implements SyncWaiter {
        private Object key;
        private Object value;

        @Override
        public Object key() {
            return this.key;
        }

        @Override
        public void key(Object key) {
            this.key = key;
        }

        @Override
        public <T> T value() {
            return (T)this.value;
        }

        @Override
        public void value(Object value) {
            this.value = value;
        }
    }
}

