/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.window;

import com.kuma.boot.common.support.dataframe.iframe.window.Sorter;
import java.util.Comparator;
import java.util.function.Function;

public class SorterBuilder<T>
implements Sorter<T> {
    protected Comparator<T> comparator;

    public SorterBuilder(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public Comparator<T> getComparator() {
        return null;
    }

    @Override
    public <U extends Comparable<? super U>> Sorter<T> sortAsc(Function<T, U> sortField) {
        this.comparator = this.comparator == null ? Comparator.comparing(sortField) : this.comparator.thenComparing(sortField);
        return this;
    }

    @Override
    public <U extends Comparable<? super U>> Sorter<T> sortDesc(Function<T, U> sortField) {
        this.comparator = this.comparator == null ? Comparator.comparing(sortField).reversed() : this.comparator.thenComparing(Comparator.comparing(sortField).reversed());
        return this;
    }

    @Override
    public Sorter<T> sort(Comparator<T> comparator) {
        this.comparator = this.comparator == null ? comparator : this.comparator.thenComparing(comparator);
        return this;
    }

    @Override
    public int compare(T o1, T o2) {
        return this.comparator.compare(o1, o2);
    }
}

