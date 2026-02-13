/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package com.kuma.boot.common.model;

import java.io.Serializable;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public class Lazy<T>
implements Supplier<T>,
Serializable {
    private transient @Nullable Supplier<? extends T> supplier;
    private volatile @Nullable T value;

    public static <T> Lazy<T> of(Supplier<T> supplier) {
        return new Lazy<T>(supplier);
    }

    private Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public @Nullable T get() {
        return this.supplier == null ? this.value : this.computeValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized @Nullable T computeValue() {
        T result = this.value;
        if (null == result) {
            Lazy lazy = this;
            synchronized (lazy) {
                result = this.value;
                if (null == result) {
                    Supplier<T> s = (Supplier<T>) this.supplier;
                    if (s == null) {
                        throw new RuntimeException("supplier is null");
                    }
                    this.value = s.get();
                    this.supplier = null;
                }
            }
        }
        return this.value;
    }
}

