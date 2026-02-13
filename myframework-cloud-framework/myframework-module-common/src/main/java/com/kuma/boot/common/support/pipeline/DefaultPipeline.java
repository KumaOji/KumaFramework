/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.pipeline;

import com.kuma.boot.common.support.pipeline.Pipeline;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DefaultPipeline<T>
implements Pipeline<T> {
    private final LinkedList<T> list = new LinkedList();

    @Override
    public Pipeline<T> addLast(T t) {
        this.list.addLast(t);
        return this;
    }

    @Override
    public Pipeline<T> addFirst(T t) {
        this.list.addFirst(t);
        return this;
    }

    @Override
    public Pipeline<T> set(int index, T t) {
        this.list.set(index, t);
        return this;
    }

    @Override
    public Pipeline<T> removeLast() {
        this.list.removeLast();
        return this;
    }

    @Override
    public Pipeline<T> removeFirst() {
        this.list.removeFirst();
        return this;
    }

    @Override
    public Pipeline<T> remove(int index) {
        this.list.remove(index);
        return this;
    }

    @Override
    public T get(int index) {
        return this.list.get(index);
    }

    @Override
    public T getFirst() {
        return this.list.getFirst();
    }

    @Override
    public T getLast() {
        return this.list.getLast();
    }

    @Override
    public List<T> list() {
        return Collections.unmodifiableList(this.list);
    }

    @Override
    public List<T> slice(int startIndex, int endIndex) {
        return this.list.subList(startIndex, endIndex);
    }
}

