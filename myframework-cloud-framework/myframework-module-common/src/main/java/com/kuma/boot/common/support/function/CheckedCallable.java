/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package com.kuma.boot.common.support.function;

import java.io.Serializable;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface CheckedCallable<T>
extends Serializable {
    public @Nullable T call() throws Throwable;
}

