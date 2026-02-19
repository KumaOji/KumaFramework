/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.office.easyexcel.easyexcelimport.refactor;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T>
extends Consumer<T> {
    @Override
    default public void accept(T t) {
        try {
            this.acceptBase(t);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void acceptBase(T var1);
}

