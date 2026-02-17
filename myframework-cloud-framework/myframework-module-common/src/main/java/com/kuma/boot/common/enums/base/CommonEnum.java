/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.enums.base;

import java.util.Objects;

/**
 * 常见枚举
 *
 * @author kuma
 * @version 2023.01
 * @since 2023-02-21 13:34:06
 */
public interface CommonEnum extends CodeEnum, SelfDescribedEnum {

    /**
     * 匹配
     * @param value 价值
     * @return boolean
     * @since 2023-02-21 13:34:08
     */
    default boolean match(String value) {
        if (value == null) {
            return false;
        }
        return value.equals(String.valueOf(getCode())) || value.equals(getName());
    }

    /**
     * 根据枚举值获取
     * @param value 枚举值
     * @param clazz 枚举类
     * @return 枚举对象
     */
    static <E extends Enum<E> & CommonEnum, T> E getByValue(T value, Class<E> clazz) {
        for (E e : clazz.getEnumConstants()) {
            if (Objects.equals(e.getCode(), value) || Objects.equals(e.getName(), value)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 根据枚举描述获取
     * @param description 枚举描述
     * @param clazz 枚举类
     * @return 枚举对象
     */
    static <E extends Enum<E> & CommonEnum> E getByDescription(String description, Class<?> clazz) {
        for (Object e : clazz.getEnumConstants()) {
            if (e instanceof CommonEnum baseEnum
                    && Objects.equals(baseEnum.getDesc(), description)) {
                @SuppressWarnings("unchecked")
                E anEnum = (E) baseEnum;
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 判断枚举值是否有效
     * @param value 枚举值
     * @param clazz 枚举类
     * @return 是否有效
     * @since 2.8.1
     */
    static <E extends Enum<E> & CommonEnum, T> boolean isValidValue(T value, Class<E> clazz) {
        return getByValue(value, clazz) != null;
    }
}
