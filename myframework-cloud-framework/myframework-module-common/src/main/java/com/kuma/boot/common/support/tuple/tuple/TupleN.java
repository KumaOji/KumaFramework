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

package com.kuma.boot.common.support.tuple.tuple;

import static java.util.Objects.requireNonNull;

/**
 * 表示有N个元素的元组类型 可迭代 不可变，线程安全
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-27 17:12:47
 */
public final class TupleN extends Tuple {

    private TupleN(final Object... args) {
        super(args);
    }

    /**
     * 反转元组
     * @return 反转后的元组
     */
    @Override
    public TupleN reverse() {
        final Object[] array = new Object[this.size()];
        this.forEachWithIndex((index, obj) -> array[array.length - 1 - index] = obj);
        return new TupleN(array);
    }

    /**
     * 从一个数组生成一个元组
     * @param args 数组
     * @return 元组
     * @see Tuples#tuple(Object...)
     */
    public static TupleN with(final Object... args) {
        requireNonNull(args, "args is null");
        return new TupleN(args);
    }
}
