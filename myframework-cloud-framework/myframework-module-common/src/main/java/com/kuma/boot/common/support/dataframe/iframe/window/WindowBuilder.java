/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.window;

import com.kuma.boot.common.support.dataframe.iframe.window.Sorter;
import com.kuma.boot.common.support.dataframe.iframe.window.Window;
import com.kuma.boot.common.support.dataframe.iframe.window.round.Range;
import com.kuma.boot.common.support.dataframe.iframe.window.round.WindowRange;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class WindowBuilder<T>
implements Window<T> {
    private List<Function<T, ?>> groupBy;
    protected Sorter<T> sorter;
    private WindowRange startRange;
    private WindowRange endRange;

    public WindowBuilder() {
    }

    public WindowBuilder(Sorter<T> comparator) {
        this.sorter = comparator;
    }

    public WindowBuilder(List<Function<T, ?>> groupBy) {
        this.groupBy = groupBy;
    }

    public WindowBuilder(WindowRange startRound, WindowRange endRound) {
        this.roundBetween(startRound, endRound);
    }

    public void initDefault() {
        if (this.startRange == null) {
            this.startRange = Range.START_ROW;
        }
        if (this.endRange == null) {
            this.endRange = Range.END_ROW;
        }
    }

    @Override
    public Comparator<T> getComparator() {
        return this.sorter == null ? null : this.sorter.getComparator();
    }

    @Override
    public WindowRange getStartRange() {
        return this.startRange;
    }

    @Override
    public WindowRange getEndRange() {
        return this.endRange;
    }

    @Override
    public List<Function<T, ?>> partitions() {
        return this.groupBy;
    }

    @Override
    public <U extends Comparable<? super U>> Window<T> sortAsc(Function<T, U> sortField) {
        if (this.sorter == null) {
            this.sorter = Sorter.sortAscBy(sortField);
        } else {
            this.sorter.sortAsc(sortField);
        }
        return this;
    }

    @Override
    public <U extends Comparable<? super U>> Window<T> sortDesc(Function<T, U> sortField) {
        if (this.sorter == null) {
            this.sorter = Sorter.sortDescBy(sortField);
        } else {
            this.sorter.sortDesc(sortField);
        }
        return this;
    }

    @Override
    public Window<T> sort(Comparator<T> comparator) {
        if (this.sorter == null) {
            this.sorter = Sorter.toSorter(comparator);
        } else {
            this.sorter.sort(comparator);
        }
        return this;
    }

    @Override
    public Window<T> roundBetween(WindowRange start, WindowRange end) {
        if (Range.END_ROW.eq(start)) {
            throw new IllegalArgumentException("The starting boundary param cannot be set to END_ROW");
        }
        if (Range.AFTER_ROW.eq(start)) {
            throw new IllegalArgumentException("The starting boundary param cannot be set to AFTER_ROW");
        }
        if (Range.START_ROW.eq(end)) {
            throw new IllegalArgumentException("The ending boundary param cannot be set to START_ROW");
        }
        if (Range.BEFORE_ROW.eq(end)) {
            throw new IllegalArgumentException("The ending boundary param cannot be set to BEFORE_ROW");
        }
        start.check();
        end.check();
        this.startRange = start;
        this.endRange = end;
        return this;
    }

    @Override
    public Window<T> roundBefore2CurrentRow(int n) {
        this.roundBetween(Range.BEFORE(n), Range.CURRENT_ROW);
        return this;
    }

    @Override
    public Window<T> roundCurrentRow2After(int n) {
        this.roundBetween(Range.CURRENT_ROW, Range.AFTER(n));
        return this;
    }

    @Override
    public Window<T> roundCurrentRow2EndRow() {
        this.roundBetween(Range.CURRENT_ROW, Range.END_ROW);
        return this;
    }

    @Override
    public Window<T> roundStartRow2CurrentRow() {
        this.roundBetween(Range.START_ROW, Range.CURRENT_ROW);
        return this;
    }

    @Override
    public Window<T> roundAllRow() {
        this.roundBetween(Range.START_ROW, Range.END_ROW);
        return this;
    }

    @Override
    public Window<T> roundBeforeAfter(int before, int after) {
        this.roundBetween(Range.BEFORE(before), Range.AFTER(after));
        return this;
    }
}

