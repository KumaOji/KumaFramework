/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.extension.util;

public class Holder<T> {
    private volatile T value;

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return this.value;
    }
}

