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

package com.kuma.boot.common.support.attr;

import java.util.Optional;
import java.util.Set;

/**
 * 属性上下文上下文
 *
 * @author kuma
 * @version 2022.09
 * @since 2022-09-02 08:22:18
 */
public interface Attribute {

    /**
     * 设置属性
     * @param key key
     * @param value 值
     * @return {@link Attribute }
     * @since 2022-09-02 08:22:18
     */
    Attribute putAttr(final String key, final Object value);

    /**
     * 获取配置属性
     * @param key key
     * @return {@link Object }
     * @since 2022-09-02 08:22:18
     */
    Object getAttr(final String key);

    /**
     * 获取配置属性-Optional
     * @param key key
     * @return {@link Optional }<{@link Object }>
     * @since 2023-04-11 13:17:11
     */
    Optional<Object> getAttrOptional(final String key);

    /**
     * 获取属性-字符串形式
     * @param key key
     * @return {@link String }
     * @since 2022-09-02 08:22:18
     */
    String getAttrString(final String key);

    /**
     * 获取属性-Boolean
     * @param key key
     * @return {@link Boolean }
     * @since 2022-09-02 08:22:18
     */
    Boolean getAttrBoolean(final String key);

    /**
     * 获取属性-Character
     * @param key key
     * @return {@link Character }
     * @since 2022-09-02 08:22:18
     */
    Character getAttrCharacter(final String key);

    /**
     * 获取属性-Byte
     * @param key key
     * @return {@link Byte }
     * @since 2022-09-02 08:22:18
     */
    Byte getAttrByte(final String key);

    /**
     * 获取属性-Short
     * @param key key
     * @return {@link Short }
     * @since 2022-09-02 08:22:18
     */
    Short getAttrShort(final String key);

    /**
     * 获取属性-Integer
     * @param key key
     * @return {@link Integer }
     * @since 2022-09-02 08:22:18
     */
    Integer getAttrInteger(final String key);

    /**
     * 获取属性-Float
     * @param key key
     * @return {@link Float }
     * @since 2022-09-02 08:22:18
     */
    Float getAttrFloat(final String key);

    /**
     * 获取属性-Double
     * @param key key
     * @return {@link Double }
     * @since 2022-09-02 08:22:18
     */
    Double getAttrDouble(final String key);

    /**
     * 获取属性-Long
     * @param key key
     * @return {@link Long }
     * @since 2022-09-02 08:22:18
     */
    Long getAttrLong(final String key);

    /**
     * 移除属性
     * @param key key
     * @return {@link Attribute }
     * @since 2022-09-02 08:22:18
     */
    Attribute removeAttr(final String key);

    /**
     * 是否包含 key
     * @param key key
     * @return boolean
     * @since 2022-09-01 09:46:30
     */
    boolean containsKey(final String key);

    /**
     * 所有的 key 集合
     * @return {@link Set }<{@link String }>
     * @since 2022-09-19 16:34:31
     */
    Set<String> keySet();
}
