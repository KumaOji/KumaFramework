/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.syncwaiting.concept;

public interface SyncWaiter {
    public Object key();

    public void key(Object var1);

    public <T> T value();

    public void value(Object var1);

    public void performWait(long var1);

    public void performNotify();
}

