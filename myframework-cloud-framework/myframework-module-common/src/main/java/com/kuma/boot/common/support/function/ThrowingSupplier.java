/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.function;

@FunctionalInterface
public interface ThrowingSupplier<T> {
    public T get() throws Exception;
}

