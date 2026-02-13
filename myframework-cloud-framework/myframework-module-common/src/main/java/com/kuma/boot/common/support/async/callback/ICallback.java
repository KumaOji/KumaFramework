/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.async.callback;

import com.kuma.boot.common.support.async.worker.WorkResult;

@FunctionalInterface
public interface ICallback<T, V> {
    default public void begin() {
    }

    public void result(boolean var1, T var2, WorkResult<V> var3);
}

