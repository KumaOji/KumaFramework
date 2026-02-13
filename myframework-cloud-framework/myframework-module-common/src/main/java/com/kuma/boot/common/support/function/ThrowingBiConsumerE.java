/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.function;

@FunctionalInterface
public interface ThrowingBiConsumerE<T, D, E extends Exception> {
    public void accept(T var1, D var2) throws E;
}

