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

package com.kuma.boot.common.support.dataframe.iframe.function;

import java.util.Objects;

/**
 * @author caizhihao
 * @param <T>
 */
@FunctionalInterface
public interface ConsumerIndex<T> {

    /**
     * Performs this operation on the given argument.
     * @param t the input argument
     * @param index The index of the current iteration
     */
    void accept(int index, T t);

    /**
     * Returns a composed {@code ConsumerIndex} that performs, in sequence, this operation
     * followed by the {@code after} operation. If performing either operation throws an
     * exception, it is relayed to the caller of the composed operation. If performing
     * this operation throws an exception, the {@code after} operation will not be
     * performed.
     * @param after the operation to perform after this operation
     * @return a composed {@code ConsumerIndex} that performs in sequence this operation
     * followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default ConsumerIndex<T> andThen(ConsumerIndex<? super T> after) {
        Objects.requireNonNull(after);
        return (index, t) -> {
            accept(index, t);
            after.accept(index, t);
        };
    }
}
