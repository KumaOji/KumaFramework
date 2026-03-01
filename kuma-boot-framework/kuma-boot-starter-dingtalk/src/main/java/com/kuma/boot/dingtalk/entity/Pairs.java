/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

public interface Pairs<K, V> {
    public K code();

    public V desc();

    public V message(Object ... var1);
}

