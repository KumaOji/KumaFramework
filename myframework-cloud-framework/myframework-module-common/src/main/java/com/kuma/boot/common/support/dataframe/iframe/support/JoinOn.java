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

package com.kuma.boot.common.support.dataframe.iframe.support;

import java.util.function.Function;

/**
 * Build Join On Condition
 *
 * @param <L> left table element
 * @param <R> right table element
 * @author caizhiho
 */
public interface JoinOn<L, R> {

    /**
     * Determine whether the association is successful
     * @param left left table element
     * @param right right table element
     */
    boolean on(L left, R right);

    /**
     * Build JoinOn based on fields
     * @param leftField left table join on field
     * @param rightField right table join on field
     */
    static <T, K, V1, V2> JoinOn<T, K> on(Function<T, V1> leftField, Function<K, V2> rightField) {
        return (t, k) -> {
            {
                if (t == null || k == null) {
                    return false;
                }
                V1 leftFieldValue = leftField.apply(t);
                V2 rightFieldValue = rightField.apply(k);

                if (leftFieldValue == null && rightFieldValue == null) {
                    return true;
                }

                if (leftFieldValue == null) {
                    // field2Value is not null so return false
                    return false;
                }
                return leftFieldValue.equals(rightFieldValue);
            }
        };
    }

    /**
     * Building Multi field Join on
     * @param leftField left table join on field
     * @param rightField left table join on field
     */
    default <V1, V2> JoinOn<L, R> thenOn(Function<L, V1> leftField, Function<R, V2> rightField) {
        return (t, k) -> on(t, k) && on(leftField, rightField).on(t, k);
    }
}
