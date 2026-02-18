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

package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.support.MaxMin;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.function.Function;

/**
 * Summary Frame data
 *
 * @author caizhihao
 */
public interface SummaryFrame<T> {

    /**
     * Sum the values of the field
     * @param function the field
     */
    <R> BigDecimal sum(Function<T, R> function);

    /**
     * average the values of the field
     * @param function the field
     */
    <R> BigDecimal avg(Function<T, R> function);

    /**
     * Finding the maximum and minimum element
     * @param function the field
     */
    <R extends Comparable<? super R>> MaxMin<T> maxMin(Function<T, R> function);

    /**
     * Finding the maximum and minimum value
     * @param function the field
     */
    <R extends Comparable<? super R>> MaxMin<R> maxMinValue(Function<T, R> function);

    /**
     * Finding the maximum element
     * @param function the field
     */
    <R extends Comparable<R>> T max(Function<T, R> function);

    /**
     * Finding the maximum value
     * @param function the field
     */
    <R extends Comparable<? super R>> R maxValue(Function<T, R> function);

    /**
     * Finding the minimum value
     * @param function the field
     */
    <R extends Comparable<? super R>> R minValue(Function<T, R> function);

    /**
     * Finding the minimum element
     * @param function the field
     */
    <R extends Comparable<R>> T min(Function<T, R> function);

    /**
     * get row count
     */
    long count();

    /**
     * Calculate the quantity after deduplication
     */
    long countDistinct(Comparator<T> comparator);

    /**
     * Calculate the quantity after deduplication
     */
    <R extends Comparable<R>> long countDistinct(Function<T, R> function);
}
