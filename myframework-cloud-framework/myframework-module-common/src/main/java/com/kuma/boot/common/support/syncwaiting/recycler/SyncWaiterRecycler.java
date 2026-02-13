/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.syncwaiting.recycler;

import com.kuma.boot.common.support.syncwaiting.concept.SyncWaiter;

public interface SyncWaiterRecycler {
    public void recycle(SyncWaiter var1);

    public SyncWaiter reuse();
}

