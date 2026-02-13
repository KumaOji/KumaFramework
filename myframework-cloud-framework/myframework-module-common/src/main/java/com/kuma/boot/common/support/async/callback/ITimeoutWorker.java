/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.async.callback;

import com.kuma.boot.common.support.async.callback.IWorker;

public interface ITimeoutWorker<T, V>
extends IWorker<T, V> {
    public long timeOut();

    public boolean enableTimeOut();
}

