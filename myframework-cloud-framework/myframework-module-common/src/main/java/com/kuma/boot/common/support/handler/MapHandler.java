/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.handler;

public interface MapHandler<K, V, O> {
    public K getKey(O var1);

    public V getValue(O var1);
}

