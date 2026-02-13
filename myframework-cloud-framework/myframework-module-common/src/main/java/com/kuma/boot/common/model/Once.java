/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package com.kuma.boot.common.model;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;

public class Once {
    private final AtomicBoolean value = new AtomicBoolean(false);

    public boolean canRun() {
        return this.value.compareAndSet(false, true);
    }

    public <T> void run(Consumer<T> consumer, T argument) {
        if (this.canRun()) {
            consumer.accept(argument);
        }
    }

    public <T, U> void run(BiConsumer<T, U> consumer, T arg1, U arg2) {
        if (this.canRun()) {
            consumer.accept(arg1, arg2);
        }
    }

    public <T, R> @Nullable R run(Function<T, R> function, T argument) {
        if (this.canRun()) {
            return function.apply(argument);
        }
        return null;
    }
}

