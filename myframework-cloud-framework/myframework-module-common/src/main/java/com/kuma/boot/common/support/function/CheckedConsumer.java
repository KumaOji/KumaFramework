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
public interface CheckedConsumer<T>
extends Serializable {
    public void accept(@Nullable T var1) throws Throwable;
}

