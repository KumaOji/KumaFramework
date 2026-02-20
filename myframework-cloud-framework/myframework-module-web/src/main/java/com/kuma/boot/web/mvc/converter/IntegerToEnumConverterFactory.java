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

package com.kuma.boot.web.mvc.converter;

import com.kuma.boot.common.enums.base.CommonEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Integer枚举转化器工厂类
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 22:10:28
 */
public class IntegerToEnumConverterFactory implements ConverterFactory<Integer, CommonEnum> {

    /** CONVERTERS */
    private static final Map<Class, Converter> CONVERTERS = new ConcurrentHashMap<>();

    /**
     * 获取一个从 Integer 转化为 T 的转换器，T 是一个泛型，有多个实现
     *
     * @param targetType targetType 转换后的类型
     * @return {@link Converter } 返回一个转化器
     * @since 2021-09-02 22:10:37
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends CommonEnum> Converter<Integer, T> getConverter(Class<T> targetType) {
        Converter<Integer, T> converter = CONVERTERS.get(targetType);
        if (converter == null) {
            converter = new com.kuma.boot.web.mvc.converter.IntegerToEnumConverter<>(targetType);
            CONVERTERS.put(targetType, converter);
        }
        return converter;
    }
}
