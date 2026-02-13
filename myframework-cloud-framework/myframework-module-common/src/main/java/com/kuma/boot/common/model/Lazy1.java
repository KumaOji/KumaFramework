/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.model;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class Lazy1<T>
implements Supplier<T> {
    private final Supplier<T> initializer;
    private final AtomicReference<T> reference = new AtomicReference();

    public Lazy1(Supplier<T> initializer) {
        this.initializer = initializer;
    }

    @Override
    public T get() {
        block1: {
            if (this.reference.get() != null) break block1;
            T value = this.initializer.get();
            while (!this.reference.compareAndSet(null, value) && (value = this.reference.get()) == null) {
            }
        }
        return this.reference.get();
    }
}

