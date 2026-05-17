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
 * 表示有1个元素的元组类型 可迭代 不可变，线程安全
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-27 17:12:34
 */
public final class Tuple1<A> extends Tuple {

    public final A first;

    private Tuple1(final A first) {
        super(first);
        this.first = first;
    }

    /**
     * 创建一个包含1个元素的元组
     * @param first 第一个元素
     * @param <A> 元素类型
     * @return 元组
     * @see Tuples#tuple(Object)
     */
    public static <A> Tuple1<A> with(final A first) {
        return new Tuple1<>(first);
    }

    /**
     * 反转元组
     * @return 反转后的元组
     */
    @Override
    public Tuple1<A> reverse() {
        return new Tuple1<>(this.first);
    }
}
