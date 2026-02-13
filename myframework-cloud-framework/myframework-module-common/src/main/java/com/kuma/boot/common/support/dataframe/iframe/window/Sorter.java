/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.window;

import com.kuma.boot.common.support.dataframe.iframe.window.SorterBuilder;
import java.util.Comparator;
import java.util.function.Function;

public interface Sorter<T>
extends Comparator<T> {
    public Comparator<T> getComparator();

    public static <T, U extends Comparable<? super U>> Sorter<T> sortAscBy(Function<T, U> sortField) {
        return new SorterBuilder<T>(Comparator.comparing(sortField));
    }

    public static <T, U extends Comparable<? super U>> Sorter<T> sortDescBy(Function<T, U> sortField) {
        return new SorterBuilder<T>(Comparator.comparing(sortField).reversed());
    }

    public static <T> Sorter<T> toSorter(Comparator<T> comparator) {
        return new SorterBuilder<T>(comparator);
    }

    public <U extends Comparable<? super U>> Sorter<T> sortAsc(Function<T, U> var1);

    public <U extends Comparable<? super U>> Sorter<T> sortDesc(Function<T, U> var1);

    public Sorter<T> sort(Comparator<T> var1);
}

