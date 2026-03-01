package com.kuma.boot.web.mvc.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import jakarta.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 参数转枚举
 *
 * @author zhanghongbin
 */
public class EnumConverterFactory implements ConverterFactory<String, Enum<?>> {
    private static final Map<Class<?>, Converter<String, ? extends Enum<?>>> CONVERTER_MAP = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked cast")
    public <T extends Enum<?>> Converter<String, T> getConverter(Class<T> targetType) {
        // 缓存转换器
        Converter<String, T> converter = (Converter<String, T>) CONVERTER_MAP.get(targetType);
        if (converter == null) {
            converter = new StringToEnumConverter<>(targetType);
            CONVERTER_MAP.put(targetType, converter);
        }
        return converter;
    }

    static class StringToEnumConverter<T extends Enum<?>> implements Converter<String, T> {

        private final Map<String, T> enumMap = new ConcurrentHashMap<>();

        StringToEnumConverter(Class<T> enumType) {
            Method method = getMethod(enumType);
            T[] enums = enumType.getEnumConstants();
            // 将值与枚举对象对应并缓存
            for (T e : enums) {
                try {
                    enumMap.put(method.invoke(e).toString(), e);
                } catch (IllegalAccessException | InvocationTargetException ex) {

                }
            }
        }

        @Override
        public T convert(@NotNull String source) {
            // 获取
            T t = enumMap.get(source);
            if (t == null) {
                throw new IllegalArgumentException("该字符串找不到对应的枚举对象 字符串:" + source);
            }
            return t;
        }
    }

    public static <T> Method getMethod(Class<T> enumType) {
        Method method = null;
        // 找到取值的方法
        // if (IEnum.class.isAssignableFrom(enumType)) {
        try {
            method = enumType.getMethod("getValue");
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(String.format("类:%s 找不到 getValue方法", enumType.getName()));
        }
        return method;
    }
}
