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

/**
 * 表示有3个元素的元组类型 可迭代 不可变，线程安全
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-27 17:12:38
 */
public final class Tuple3<A, B, C> extends Tuple {

    public final A first;

    public final B second;

    public final C third;

    private Tuple3(final A first, final B second, final C third) {
        super(first, second, third);
        this.first = first;
        this.second = second;
        this.third = third;
    }

    /**
     * 创建一个包含3个元素的元组
     * @param first 第一个元素
     * @param second 第二个元素
     * @param third 第三个元素
     * @param <A> 第一个元素类型
     * @param <B> 第二个元素类型
     * @param <C> 第三个元素类型
     * @return 元组
     * @see Tuples#tuple(Object, Object, Object)
     */
    public static <A, B, C> Tuple3<A, B, C> with(final A first, final B second, final C third) {
        return new Tuple3<>(first, second, third);
    }

    /**
     * 反转元组
     * @return 反转后的元组
     */
    @Override
    public Tuple3<C, B, A> reverse() {
        return new Tuple3<>(this.third, this.second, this.first);
    }
}
