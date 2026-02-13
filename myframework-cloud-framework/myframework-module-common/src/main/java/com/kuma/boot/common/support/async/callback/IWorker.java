/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.async.callback;

import com.kuma.boot.common.support.async.wrapper.WorkerWrapper;
import java.util.Map;

@FunctionalInterface
public interface IWorker<T, V> {
    public V action(T var1, Map<String, WorkerWrapper<?, ?>> var2);

    default public V defaultValue() {
        return null;
    }
}

