/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.function;

@FunctionalInterface
public interface ThrowingFunction<T, R> {
    public R apply(T var1) throws Exception;
}

