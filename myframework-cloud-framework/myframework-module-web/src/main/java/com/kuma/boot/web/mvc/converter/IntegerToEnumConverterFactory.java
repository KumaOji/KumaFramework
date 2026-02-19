/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.enums.base.CommonEnum
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.core.convert.converter.ConverterFactory
 */
package com.kuma.boot.web.mvc.converter;

import com.kuma.boot.common.enums.base.CommonEnum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

public class IntegerToEnumConverterFactory
implements ConverterFactory<Integer, CommonEnum> {
    private static final Map<Class, Converter> CONVERTERS = new ConcurrentHashMap<Class, Converter>();

    public <T extends CommonEnum> Converter<Integer, T> getConverter(Class<T> targetType) {
        IntegerToEnumConverter<T> converter = CONVERTERS.get(targetType);
        if (converter == null) {
            converter = new IntegerToEnumConverter<T>(targetType);
            CONVERTERS.put(targetType, converter);
        }
        return converter;
    }
}

