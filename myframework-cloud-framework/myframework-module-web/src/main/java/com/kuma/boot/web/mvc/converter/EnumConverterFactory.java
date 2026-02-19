/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  javax.validation.constraints.NotNull
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.core.convert.converter.ConverterFactory
 */
package com.kuma.boot.web.mvc.converter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.validation.constraints.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

public class EnumConverterFactory
implements ConverterFactory<String, Enum<?>> {
    private static final Map<Class<?>, Converter<String, ? extends Enum<?>>> CONVERTER_MAP = new ConcurrentHashMap();

    public <T extends Enum<?>> Converter<String, T> getConverter(Class<T> targetType) {
        Converter<String, ? extends Enum<?>> converter = CONVERTER_MAP.get(targetType);
        if (converter == null) {
            converter = new StringToEnumConverter(targetType);
            CONVERTER_MAP.put(targetType, converter);
        }
        return converter;
    }

    public static <T> Method getMethod(Class<T> enumType) {
        Method method = null;
        try {
            method = enumType.getMethod("getValue", new Class[0]);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(String.format("\u7c7b:%s \u627e\u4e0d\u5230 getValue\u65b9\u6cd5", enumType.getName()));
        }
        return method;
    }

    static class StringToEnumConverter<T extends Enum<?>>
    implements Converter<String, T> {
        private final Map<String, T> enumMap = new ConcurrentHashMap<String, T>();

        StringToEnumConverter(Class<T> enumType) {
            Enum[] enums;
            Method method = EnumConverterFactory.getMethod(enumType);
            for (Enum e : enums = (Enum[])enumType.getEnumConstants()) {
                try {
                    this.enumMap.put(method.invoke((Object)e, new Object[0]).toString(), e);
                }
                catch (IllegalAccessException | InvocationTargetException reflectiveOperationException) {
                    // empty catch block
                }
            }
        }

        public T convert(@NotNull String source) {
            Enum t = (Enum)this.enumMap.get(source);
            if (t == null) {
                throw new IllegalArgumentException("\u8be5\u5b57\u7b26\u4e32\u627e\u4e0d\u5230\u5bf9\u5e94\u7684\u679a\u4e3e\u5bf9\u8c61 \u5b57\u7b26\u4e32:" + source);
            }
            return (T)t;
        }
    }
}

