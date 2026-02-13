/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.handler;

import java.util.Map;

public interface MapEntryHandler<K, V, T> {
    public T handler(Map.Entry<K, V> var1);
}

