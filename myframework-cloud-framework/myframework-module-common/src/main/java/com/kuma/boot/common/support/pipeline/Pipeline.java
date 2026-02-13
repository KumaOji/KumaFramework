/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.pipeline;

import java.util.List;

public interface Pipeline<T> {
    public Pipeline<T> addLast(T var1);

    public Pipeline<T> addFirst(T var1);

    public Pipeline<T> set(int var1, T var2);

    public Pipeline<T> removeLast();

    public Pipeline<T> removeFirst();

    public Pipeline<T> remove(int var1);

    public T get(int var1);

    public T getFirst();

    public T getLast();

    public List<T> list();

    public List<T> slice(int var1, int var2);
}

