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

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjUtil;
import com.kuma.boot.common.enums.base.CommonEnum;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

/**
 * Integer枚举转化器
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-03 08:03:40
 */
public class IntegerToEnumConverter<T extends CommonEnum> implements Converter<Integer, T> {

    private final Map<Integer, T> enumMap = MapUtil.newHashMap();

    public IntegerToEnumConverter(Class<T> enumType) {
        T[] enums = enumType.getEnumConstants();
        for (T e : enums) {
            enumMap.put(e.getCode(), e);
        }
    }

    @Override
    public T convert(Integer source) {
        T t = enumMap.get(source);
        if (ObjUtil.isNull(t)) {
            throw new IllegalArgumentException("无法匹配对应的类型");
        }
        return t;
    }
}
