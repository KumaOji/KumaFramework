/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.window;

import com.kuma.boot.common.support.dataframe.iframe.window.Sorter;
import com.kuma.boot.common.support.dataframe.iframe.window.WindowBuilder;
import com.kuma.boot.common.support.dataframe.iframe.window.round.Range;
import com.kuma.boot.common.support.dataframe.iframe.window.round.WindowRange;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public interface Window<T> {
    @SafeVarargs
    public static <T> Window<T> groupBy(Function<T, ?> ... groupField) {
        return new WindowBuilder<T>(Arrays.asList(groupField));
    }

    public static <T, U extends Comparable<? super U>> Window<T> sortAscBy(Function<T, U> sortField) {
        return new WindowBuilder<T>(Sorter.sortAscBy(sortField));
    }

    public static <T, U extends Comparable<? super U>> Window<T> sortDescBy(Function<T, U> sortField) {
        return new WindowBuilder<T>(Sorter.sortDescBy(sortField));
    }

    public static <T> Window<T> sortBy(Comparator<T> comparator) {
        return new WindowBuilder<T>(Sorter.toSorter(comparator));
    }

    public static <T> Window<T> sortBy(Sorter<T> sorter) {
        return new WindowBuilder<T>(sorter);
    }

    public static <T> Window<T> roundBetweenBy(WindowRange start, WindowRange end) {
        return new WindowBuilder(start, end);
    }

    public static <T> Window<T> roundBefore2CurrentRowBy(int n) {
        return new WindowBuilder(Range.BEFORE(n), Range.CURRENT_ROW);
    }

    public static <T> Window<T> roundCurrentRow2AfterBy(int n) {
        return new WindowBuilder(Range.CURRENT_ROW, Range.AFTER(n));
    }

    public static <T> Window<T> roundCurrentRow2EndRowBy() {
        return new WindowBuilder(Range.CURRENT_ROW, Range.END_ROW);
    }

    public static <T> Window<T> roundStartRow2CurrentRowBy() {
        return new WindowBuilder(Range.START_ROW, Range.CURRENT_ROW);
    }

    public static <T> Window<T> roundAllRowBy() {
        return new WindowBuilder(Range.START_ROW, Range.END_ROW);
    }

    public static <T> Window<T> roundBeforeAfterBy(int before, int after) {
        return new WindowBuilder(Range.BEFORE(before), Range.AFTER(after));
    }

    public <U extends Comparable<? super U>> Window<T> sortAsc(Function<T, U> var1);

    public <U extends Comparable<? super U>> Window<T> sortDesc(Function<T, U> var1);

    public Window<T> sort(Comparator<T> var1);

    public Window<T> roundBetween(WindowRange var1, WindowRange var2);

    public Window<T> roundBefore2CurrentRow(int var1);

    public Window<T> roundCurrentRow2After(int var1);

    public Window<T> roundCurrentRow2EndRow();

    public Window<T> roundStartRow2CurrentRow();

    public Window<T> roundAllRow();

    public Window<T> roundBeforeAfter(int var1, int var2);

    public List<Function<T, ?>> partitions();

    public Comparator<T> getComparator();

    public WindowRange getStartRange();

    public WindowRange getEndRange();
}

