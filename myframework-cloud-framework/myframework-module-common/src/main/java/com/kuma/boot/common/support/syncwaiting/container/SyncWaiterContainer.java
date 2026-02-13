/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.syncwaiting.container;

import com.kuma.boot.common.support.syncwaiting.concept.SyncWaiter;

public interface SyncWaiterContainer {
    public boolean contains(Object var1);

    public SyncWaiter find(Object var1);

    public void add(SyncWaiter var1);

    public SyncWaiter remove(Object var1);
}

