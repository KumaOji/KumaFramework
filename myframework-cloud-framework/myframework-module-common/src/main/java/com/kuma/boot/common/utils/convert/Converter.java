/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.ReflectUtil
 *  org.jspecify.annotations.Nullable
 *  org.springframework.cglib.core.Converter
 *  org.springframework.core.convert.TypeDescriptor
 */
package com.kuma.boot.common.utils.convert;

import cn.hutool.core.util.ReflectUtil;
import com.kuma.boot.common.support.function.CheckedFunction;
import com.kuma.boot.common.support.function.Unchecked;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.convert.ConvertUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.reflect.ClassUtils;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.TypeDescriptor;

public class Converter
implements org.springframework.cglib.core.Converter {
    private static final ConcurrentMap<String, TypeDescriptor> TYPE_CACHE = new ConcurrentHashMap<String, TypeDescriptor>();
    private final Class<?> sourceClazz;
    private final Class<?> targetClazz;

    public Converter(Class<?> sourceClazz, Class<?> targetClazz) {
        this.sourceClazz = sourceClazz;
        this.targetClazz = targetClazz;
    }

    public @Nullable Object convert(@Nullable Object value, Class target, Object fieldName) {
        if (value == null) {
            return null;
        }
        if (ClassUtils.isAssignableValue((Class)target, (Object)value)) {
            return value;
        }
        try {
            TypeDescriptor targetDescriptor = Converter.getTypeDescriptor(this.targetClazz, (String)fieldName);
            if (Map.class.isAssignableFrom(this.sourceClazz)) {
                return ConvertUtils.convert(value, targetDescriptor);
            }
            TypeDescriptor sourceDescriptor = Converter.getTypeDescriptor(this.sourceClazz, (String)fieldName);
            return ConvertUtils.convert(value, sourceDescriptor, targetDescriptor);
        }
        catch (Throwable e) {
            LogUtils.warn("MicaConverter error", e);
            return null;
        }
    }

    private static TypeDescriptor getTypeDescriptor(Class<?> clazz, String fieldName) {
        String srcCacheKey = clazz.getName() + fieldName;
        CheckedFunction<String, TypeDescriptor> uncheckedFunction = key -> {
            Field field = ReflectUtil.getField((Class)clazz, (String)fieldName);
            if (field == null) {
                throw new NoSuchFieldException(fieldName);
            }
            return new TypeDescriptor(field);
        };
        return CollectionUtils.computeIfAbsent(TYPE_CACHE, srcCacheKey, Unchecked.function(uncheckedFunction));
    }
}

