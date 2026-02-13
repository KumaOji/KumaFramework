/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.function;

import java.util.Objects;

@FunctionalInterface
public interface ConsumerIndex<T> {
    public void accept(int var1, T var2);

    default public ConsumerIndex<T> andThen(ConsumerIndex<? super T> after) {
        Objects.requireNonNull(after);
        return (index, t) -> {
            this.accept(index, t);
            after.accept(index, t);
        };
    }
}

