/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.function;

import java.io.Serializable;

@FunctionalInterface
public interface CheckedPredicate<T>
extends Serializable {
    public boolean test(T var1) throws Throwable;
}

