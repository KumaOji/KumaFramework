/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.function;

import java.io.Serializable;

@FunctionalInterface
public interface CheckedComparator<T>
extends Serializable {
    public int compare(T var1, T var2) throws Throwable;
}

