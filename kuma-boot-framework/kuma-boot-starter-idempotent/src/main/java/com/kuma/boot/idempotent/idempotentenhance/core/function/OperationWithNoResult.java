/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.idempotent.idempotentenhance.core.function;

@FunctionalInterface
public interface OperationWithNoResult {
    public void operation() throws Throwable;
}

