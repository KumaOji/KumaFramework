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

package com.kuma.boot.common.support.dataframe.iframe.support;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

/**
 * support null value to compare. if values is null sort to the end in sort asc if values
 * is null sort to the start in sort desc
 *
 * @author caizhihao
 * @param <T>
 */
public interface NullFirstComparator<T> extends Comparator<T> {

    /**
     * simplify build Comparator by keyExtractor
     */
    static <T, U extends Comparable<? super U>> Comparator<T> comparing(
            Function<? super T, ? extends U> keyExtractor) {
        Objects.requireNonNull(keyExtractor);
        return (t1, t2) -> {
            if (t1 == null && t2 == null) {
                return 0;
            }
            if (t1 == null) {
                return 1;
            }
            if (t2 == null) {
                return -1;
            }
            U t1Value = keyExtractor.apply(t1);
            U t2Value = keyExtractor.apply(t2);
            if (t1Value == null && t2Value == null) {
                return 0;
            }
            if (t1Value == null) {
                return 1;
            }
            if (t2Value == null) {
                return -1;
            }
            return t1Value.compareTo(t2Value);
        };
    }
}
