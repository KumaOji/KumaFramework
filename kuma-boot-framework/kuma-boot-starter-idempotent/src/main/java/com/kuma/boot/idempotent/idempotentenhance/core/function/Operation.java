/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.idempotent.idempotentenhance.core.function;

@FunctionalInterface
public interface Operation<T> {
    public T operation() throws Throwable;
}

