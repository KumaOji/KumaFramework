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

import com.kuma.boot.common.support.tuple.ValueFour;
import com.kuma.boot.common.support.tuple.ValueOne;
import com.kuma.boot.common.support.tuple.ValueThree;
import com.kuma.boot.common.support.tuple.ValueTwo;

/**
 * 四元的
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-27 17:12:19
 */
public class Quatenary<A, B, C, D> extends AbstractTuple
        implements ValueOne<A>, ValueTwo<B>, ValueThree<C>, ValueFour<D> {

    /** 第一个元素 */
    private final A a;

    /** 第二个元素 */
    private final B b;

    /** 第三个元素 */
    private final C c;

    /** 第四个元素 */
    private final D d;

    /**
     * 构造器
     * @param a 第一个元素
     * @param b 第二个元素
     * @param c 第三个元素
     * @param d 第四个元素
     */
    public Quatenary(A a, B b, C c, D d) {
        super(a, b, c, d);
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    /**
     * 初始化三元运算符
     * @param a 第一个元素
     * @param b 第二个元素
     * @param c 第三个元素
     * @param d 第四个元素
     * @param <A> 泛型1
     * @param <B> 泛型2
     * @param <C> 泛型3
     * @param <D> 泛型4
     * @return 结果
     */
    public static <A, B, C, D> Quatenary<A, B, C, D> of(A a, B b, C c, D d) {
        return new Quatenary<>(a, b, c, d);
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
    public C getValueThree() {
        return this.c;
    }

    @Override
    public D getValueFour() {
        return this.d;
    }

    @Override
    public String toString() {
        return "Quatenary{" + "a=" + a + ", b=" + b + ", c=" + c + ", d=" + d + '}';
    }
}
