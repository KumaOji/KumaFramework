/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.async;

@FunctionalInterface
public interface SupplierWithException<R, E extends Throwable> {
    public R get() throws E;
}

