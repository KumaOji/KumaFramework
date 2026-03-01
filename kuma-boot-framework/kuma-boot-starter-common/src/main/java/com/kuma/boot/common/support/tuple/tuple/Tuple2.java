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

package com.kuma.boot.common.support.tuple.tuple;

/**
 * 表示有2个元素的元组类型 可迭代 不可变，线程安全
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-27 17:12:36
 */
public final class Tuple2<A, B> extends Tuple {

    public final A first;

    public final B second;

    private Tuple2(final A first, final B second) {
        super(first, second);
        this.first = first;
        this.second = second;
    }

    /**
     * 创建一个包含2个元素的元组
     * @param first 第一个元素
     * @param second 第二个元素
     * @param <A> 第一个元素类型
     * @param <B> 第二个元素类型
     * @return 元组
     * @see Tuples#tuple(Object, Object)
     */
    public static <A, B> Tuple2<A, B> with(final A first, final B second) {
        return new Tuple2<>(first, second);
    }

    /**
     * 反转元组
     * @return 反转后的元组
     */
    @Override
    public Tuple2<B, A> reverse() {
        return new Tuple2<>(this.second, this.first);
    }
}
