/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.function;

@FunctionalInterface
public interface ThrowingBiFunction<T, D, R> {
    public R apply(T var1, D var2) throws Exception;
}

