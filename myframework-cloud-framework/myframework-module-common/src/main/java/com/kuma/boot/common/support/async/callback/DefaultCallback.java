/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.async.callback;

import com.kuma.boot.common.support.async.callback.ICallback;
import com.kuma.boot.common.support.async.worker.WorkResult;

public class DefaultCallback<T, V>
implements ICallback<T, V> {
    @Override
    public void begin() {
    }

    @Override
    public void result(boolean success, T param, WorkResult<V> workResult) {
    }
}

