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

package com.kuma.boot.common.support.dataframe.iframe;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface WhereIFrame<T> {

    /**
     * filter by predicate
     * @param predicate the predicate
     */
    IFrame<T> where(Predicate<? super T> predicate);

    /**
     * Filter field values that are null, If it is string compatible, null and ''
     * situations
     * @param function the filter field
     * @param <R> the filter field type
     */
    <R> IFrame<T> whereNull(Function<T, R> function);

    /**
     * Filter field values that are not null,If it is string compatible, null and ''
     * situations
     * @param function the filter field
     * @param <R> the filter field type
     */
    <R> IFrame<T> whereNotNull(Function<T, R> function);

    /**
     * Screening within the interval,front closed and back closed. [start,end] [start,end]
     * @param function the filter field
     * @param start start value
     * @param end end value
     */
    <R extends Comparable<R>> IFrame<T> whereBetween(Function<T, R> function, R start, R end);

    /**
     * Screening within the interval , front open and back open (start,end)
     * @param function the filter field
     * @param start start value
     * @param end end value
     */
    <R extends Comparable<R>> IFrame<T> whereBetweenN(Function<T, R> function, R start, R end);

    /**
     * Screening within the interval , front open and back close (start,end]
     * @param function the filter field
     * @param start start value
     * @param end end value
     */
    <R extends Comparable<R>> IFrame<T> whereBetweenR(Function<T, R> function, R start, R end);

    /**
     * Screening within the interval , front close and back open [start,end)
     * @param function the filter field
     * @param start start value
     * @param end end value
     */
    <R extends Comparable<R>> IFrame<T> whereBetweenL(Function<T, R> function, R start, R end);

    /**
     * Out of range screening, (front closed and back closed) [start,end]
     * @param function the filter field
     * @param start start value
     * @param end end value
     */
    <R extends Comparable<R>> IFrame<T> whereNotBetween(Function<T, R> function, R start, R end);

    /**
     * Out of range screening, (front open and back open) (start,end)
     * @param function the filter field
     * @param start start value
     * @param end end value
     */
    <R extends Comparable<R>> IFrame<T> whereNotBetweenN(Function<T, R> function, R start, R end);

    /**
     * The query value is within the specified range
     * @param function the filter field
     * @param list specified range
     */
    <R> IFrame<T> whereIn(Function<T, R> function, List<R> list);

    /**
     * The query value is outside the specified range
     * @param function the filter field
     * @param list specified range
     */
    <R> IFrame<T> whereNotIn(Function<T, R> function, List<R> list);

    /**
     * filter true by predicate
     */
    IFrame<T> whereTrue(Predicate<T> predicate);

    /**
     * filter not true by predicate
     */
    IFrame<T> whereNotTrue(Predicate<T> predicate);

    /**
     * Filter equals
     * @param function the field
     * @param value need value
     */
    <R> IFrame<T> whereEq(Function<T, R> function, R value);

    /**
     * Filter not equals
     * @param function the field
     * @param value not need value
     */
    <R> IFrame<T> whereNotEq(Function<T, R> function, R value);

    /**
     * Filter Greater than value
     * @param function the field
     * @param value not need value
     */
    <R extends Comparable<R>> IFrame<T> whereGt(Function<T, R> function, R value);

    /**
     * Filter Greater than or equal to
     * @param function the field
     * @param value not need value
     */
    <R extends Comparable<R>> IFrame<T> whereGe(Function<T, R> function, R value);

    /**
     * Filter LESS than value
     * @param function the field
     * @param value not need value
     */
    <R extends Comparable<R>> IFrame<T> whereLt(Function<T, R> function, R value);

    /**
     * Filter less than or equal to
     * @param function the field
     * @param value not need value
     */
    <R extends Comparable<R>> IFrame<T> whereLe(Function<T, R> function, R value);

    /**
     * Fuzzy query contains specified values
     * @param function the field
     * @param value query value
     */
    <R> IFrame<T> whereLike(Function<T, R> function, R value);

    /**
     * Fuzzy query not contains specified values
     * @param function the field
     * @param value query value
     */
    <R> IFrame<T> whereNotLike(Function<T, R> function, R value);

    /**
     * prefix fuzzy query contains specified values
     * @param function the field
     * @param value query value
     */
    <R> IFrame<T> whereLikeLeft(Function<T, R> function, R value);

    /**
     * suffix fuzzy query contains specified values
     * @param function the field
     * @param value query value
     */
    <R> IFrame<T> whereLikeRight(Function<T, R> function, R value);
}
