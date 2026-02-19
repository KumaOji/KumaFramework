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

package com.kuma.boot.common.support.tuple.impl;

import com.kuma.boot.common.support.tuple.ValueOne;
import com.kuma.boot.common.support.tuple.ValueTwo;

/**
 * 二元的
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-27 17:12:16
 */
public class Pair<A, B> extends AbstractTuple implements ValueOne<A>, ValueTwo<B> {

    /** 第一个元素 */
    private final A a;

    /** 第二个元素 */
    private final B b;

    /**
     * 初始化二元运算符
     * @param a 第一个元素
     * @param b 第二个元素
     */
    public Pair(A a, B b) {
        super(a, b);
        this.a = a;
        this.b = b;
    }

    /**
     * 初始化二元运算符
     * @param a 第一个元素
     * @param b 第二个元素
     * @param <A> 泛型1
     * @param <B> 泛型2
     * @return 结果
     */
    public static <A, B> Pair<A, B> of(A a, B b) {
        return new Pair<>(a, b);
    }

    @Override
    public A getValueOne() {
        return this.a;
    }

    @Override
    public B getValueTwo() {
        return this.b;
    }

    @Override
    public String toString() {
        return "Pair{" + "a=" + a + ", b=" + b + '}';
    }
}
