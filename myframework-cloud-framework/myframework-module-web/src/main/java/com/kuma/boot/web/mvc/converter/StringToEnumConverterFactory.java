/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.map.MapUtil
 *  com.kuma.boot.common.enums.base.CommonEnum
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.core.convert.converter.ConverterFactory
 */
package com.kuma.boot.web.mvc.converter;

import cn.hutool.core.map.MapUtil;
import com.kuma.boot.common.enums.base.CommonEnum;

import java.util.Map;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

public class StringToEnumConverterFactory
implements ConverterFactory<String, CommonEnum> {
    private static final Map<Class, Converter> CONVERTERS = MapUtil.newHashMap();

    public <T extends CommonEnum> Converter<String, T> getConverter(Class<T> targetType) {
        StringToEnumConverter<T> converter = CONVERTERS.get(targetType);
        if (converter == null) {
            converter = new StringToEnumConverter<T>(targetType);
            CONVERTERS.put(targetType, converter);
        }
        return converter;
    }
}

