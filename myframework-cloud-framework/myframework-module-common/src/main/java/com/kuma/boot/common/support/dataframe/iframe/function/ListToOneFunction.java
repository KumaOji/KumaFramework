/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.function;

import java.util.List;

@FunctionalInterface
public interface ListToOneFunction<T> {
    public T apply(List<T> var1);
}

