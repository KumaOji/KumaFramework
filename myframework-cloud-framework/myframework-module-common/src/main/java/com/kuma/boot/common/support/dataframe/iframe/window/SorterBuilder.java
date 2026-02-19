/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

import java.util.Comparator;
import java.util.function.Function;

/**
 * @author caizhiao
 */
public class SorterBuilder<T> implements Sorter<T> {

    protected Comparator<T> comparator;

    public SorterBuilder(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public Comparator<T> getComparator() {
        return null;
    }

    public <U extends Comparable<? super U>> Sorter<T> sortAsc(Function<T, U> sortField) {
        if (this.comparator == null) {
            this.comparator = Comparator.comparing(sortField);
        } else {
            this.comparator = this.comparator.thenComparing(sortField);
        }
        return this;
    }

    public <U extends Comparable<? super U>> Sorter<T> sortDesc(Function<T, U> sortField) {
        if (this.comparator == null) {
            this.comparator = Comparator.comparing(sortField).reversed();
        } else {
            this.comparator =
                    this.comparator.thenComparing(Comparator.comparing(sortField).reversed());
        }
        return this;
    }

    @Override
    public Sorter<T> sort(Comparator<T> comparator) {
        if (this.comparator == null) {
            this.comparator = comparator;
        } else {
            this.comparator = this.comparator.thenComparing(comparator);
        }
        return this;
    }

    @Override
    public int compare(T o1, T o2) {
        return comparator.compare(o1, o2);
    }
}
