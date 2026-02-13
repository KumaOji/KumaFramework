/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.syncwaiting.recycler;

import com.kuma.boot.common.support.syncwaiting.concept.SyncWaiter;
import com.kuma.boot.common.support.syncwaiting.recycler.SyncWaiterRecycler;

public class DisposableSyncWaiterRecycler
implements SyncWaiterRecycler {
    @Override
    public void recycle(SyncWaiter waiter) {
    }

    @Override
    public SyncWaiter reuse() {
        return null;
    }
}

