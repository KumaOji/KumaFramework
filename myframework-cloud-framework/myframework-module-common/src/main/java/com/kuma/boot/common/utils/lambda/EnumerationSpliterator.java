/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.lambda;

import java.util.Enumeration;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public class EnumerationSpliterator<T>
extends Spliterators.AbstractSpliterator<T> {
    private final Enumeration<T> enumeration;

    protected EnumerationSpliterator(Enumeration<T> enumeration, int additionalCharacteristics) {
        super(Long.MAX_VALUE, additionalCharacteristics);
        this.enumeration = enumeration;
    }

    public static <T> Spliterator<T> spliteratorUnknownSize(Enumeration<T> enumeration) {
        return new EnumerationSpliterator<T>(Objects.requireNonNull(enumeration), 16);
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (this.enumeration.hasMoreElements()) {
            action.accept(this.enumeration.nextElement());
            return true;
        }
        return false;
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        while (this.enumeration.hasMoreElements()) {
            action.accept(this.enumeration.nextElement());
        }
    }
}

