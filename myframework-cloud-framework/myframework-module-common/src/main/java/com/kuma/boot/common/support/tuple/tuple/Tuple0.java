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
 * 表示有0个元素的元组类型
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-27 17:12:32
 */
public final class Tuple0 extends Tuple {

    private static final Object[] EMPTY = new Object[] {};

    private static final Tuple0 INSTANCE = new Tuple0();

    private Tuple0() {
        super(EMPTY);
    }

    /**
     * 反转元组
     * @return 反转后的元组
     */
    @Override
    public Tuple0 reverse() {
        return this;
    }

    /**
     * 得到一个包含0个元素的元组
     * @return 元组
     * @see Tuples#tuple()
     */
    public static Tuple0 with() {
        return INSTANCE;
    }
}
