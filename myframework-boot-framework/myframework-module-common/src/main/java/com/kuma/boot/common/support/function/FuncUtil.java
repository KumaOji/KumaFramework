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

package com.kuma.boot.common.support.function;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * func跑龙套
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-05-26 14:37:25
 */
public class FuncUtil {

    /**
     * 谓词
     * @param t t
     * @param predicate 谓词
     * @param defaultValue 默认值
     * @return {@link T }
     * @since 2022-05-26 14:39:29
     */
    public static <T> T predicate(T t, Predicate<T> predicate, T defaultValue) {
        return predicate.test(t) ? defaultValue : t;
    }

    /**
     * 谓词
     * @param t t
     * @param supplier 供应商
     * @param defaultValue 默认值
     * @return {@link T }
     * @since 2022-05-26 15:06:52
     */
    public static <T> T predicate(T t, Supplier<Boolean> supplier, T defaultValue) {
        return supplier.get() ? defaultValue : t;
    }
}
