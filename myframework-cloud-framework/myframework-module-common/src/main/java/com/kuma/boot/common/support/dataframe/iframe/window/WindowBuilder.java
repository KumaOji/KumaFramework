/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.support.dataframe.iframe.window;

import com.kuma.boot.common.support.dataframe.iframe.window.round.Range;
import com.kuma.boot.common.support.dataframe.iframe.window.round.WindowRange;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * @author caizhiao
 */
public class WindowBuilder<T> implements Window<T> {

    private List<Function<T, ?>> groupBy;

    protected Sorter<T> sorter;

    private WindowRange startRange;

    private WindowRange endRange;

    public WindowBuilder() {}

    public WindowBuilder(Sorter<T> comparator) {
        this.sorter = comparator;
    }

    public WindowBuilder(List<Function<T, ?>> groupBy) {
        this.groupBy = groupBy;
    }

    public WindowBuilder(WindowRange startRound, WindowRange endRound) {
        roundBetween(startRound, endRound);
    }

    public void initDefault() {
        if (startRange == null) {
            this.startRange = Range.START_ROW;
        }

        if (endRange == null) {
            this.endRange = Range.END_ROW;
        }
    }

    @Override
    public Comparator<T> getComparator() {
        return sorter == null ? null : sorter.getComparator();
    }

    public WindowRange getStartRange() {
        return startRange;
    }

    public WindowRange getEndRange() {
        return endRange;
    }

    public List<Function<T, ?>> partitions() {
        return groupBy;
    }

    public <U extends Comparable<? super U>> Window<T> sortAsc(Function<T, U> sortField) {
        if (sorter == null) {
            this.sorter = Sorter.sortAscBy(sortField);
        } else {
            sorter.sortAsc(sortField);
        }
        return this;
    }

    public <U extends Comparable<? super U>> Window<T> sortDesc(Function<T, U> sortField) {
        if (sorter == null) {
            this.sorter = Sorter.sortDescBy(sortField);
        } else {
            sorter.sortDesc(sortField);
        }
        return this;
    }

    @Override
    public Window<T> sort(Comparator<T> comparator) {
        if (sorter == null) {
            this.sorter = Sorter.toSorter(comparator);
        } else {
            sorter.sort(comparator);
        }
        return this;
    }

    @Override
    public Window<T> roundBetween(WindowRange start, WindowRange end) {
        if (Range.END_ROW.eq(start)) {
            throw new IllegalArgumentException(
                    "The starting boundary param cannot be set to END_ROW");
        }
        if (Range.AFTER_ROW.eq(start)) {
            throw new IllegalArgumentException(
                    "The starting boundary param cannot be set to AFTER_ROW");
        }

        if (Range.START_ROW.eq(end)) {
            throw new IllegalArgumentException(
                    "The ending boundary param cannot be set to START_ROW");
        }

        if (Range.BEFORE_ROW.eq(end)) {
            throw new IllegalArgumentException(
                    "The ending boundary param cannot be set to BEFORE_ROW");
        }

        start.check();
        end.check();

        this.startRange = start;
        this.endRange = end;
        return this;
    }

    @Override
    public Window<T> roundBefore2CurrentRow(int n) {
        roundBetween(Range.BEFORE(n), Range.CURRENT_ROW);
        return this;
    }

    @Override
    public Window<T> roundCurrentRow2After(int n) {
        roundBetween(Range.CURRENT_ROW, Range.AFTER(n));
        return this;
    }

    @Override
    public Window<T> roundCurrentRow2EndRow() {
        roundBetween(Range.CURRENT_ROW, Range.END_ROW);
        return this;
    }

    @Override
    public Window<T> roundStartRow2CurrentRow() {
        roundBetween(Range.START_ROW, Range.CURRENT_ROW);
        return this;
    }

    @Override
    public Window<T> roundAllRow() {
        roundBetween(Range.START_ROW, Range.END_ROW);
        return this;
    }

    @Override
    public Window<T> roundBeforeAfter(int before, int after) {
        roundBetween(Range.BEFORE(before), Range.AFTER(after));
        return this;
    }
}
