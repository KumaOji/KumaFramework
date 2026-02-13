/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.reflect;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public final class PrimitiveUtils {
    private static final Map<Class<?>, Class<?>> TYPE_MAP = new IdentityHashMap(8);
    private static final Map<Class, Class> PRIMITIVE_REFERENCE_MAP = new HashMap<Class, Class>();
    private static final Map<Class, Object> PRIMITIVE_DEFAULT_MAP = new HashMap<Class, Object>();

    private PrimitiveUtils() {
    }

    public static Class<?> getPrimitiveType(Class<?> classType) {
        return TYPE_MAP.get(classType);
    }

    public static Class getReferenceType(Class clazz) {
        if (clazz.isPrimitive()) {
            return PRIMITIVE_REFERENCE_MAP.get(clazz);
        }
        return clazz;
    }

    public static Object getDefaultValue(Class clazz) {
        return PRIMITIVE_DEFAULT_MAP.get(clazz);
    }

    static {
        PRIMITIVE_REFERENCE_MAP.put(Integer.TYPE, Integer.class);
        PRIMITIVE_REFERENCE_MAP.put(Boolean.TYPE, Boolean.class);
        PRIMITIVE_REFERENCE_MAP.put(Byte.TYPE, Byte.class);
        PRIMITIVE_REFERENCE_MAP.put(Character.TYPE, Character.class);
        PRIMITIVE_REFERENCE_MAP.put(Short.TYPE, Short.class);
        PRIMITIVE_REFERENCE_MAP.put(Long.TYPE, Long.class);
        PRIMITIVE_REFERENCE_MAP.put(Float.TYPE, Float.class);
        PRIMITIVE_REFERENCE_MAP.put(Double.TYPE, Double.class);
        PRIMITIVE_REFERENCE_MAP.put(Void.TYPE, Void.class);
        TYPE_MAP.put(Boolean.class, Boolean.TYPE);
        TYPE_MAP.put(Byte.class, Byte.TYPE);
        TYPE_MAP.put(Character.class, Character.TYPE);
        TYPE_MAP.put(Double.class, Double.TYPE);
        TYPE_MAP.put(Float.class, Float.TYPE);
        TYPE_MAP.put(Integer.class, Integer.TYPE);
        TYPE_MAP.put(Long.class, Long.TYPE);
        TYPE_MAP.put(Short.class, Short.TYPE);
        PRIMITIVE_DEFAULT_MAP.put(Integer.TYPE, 0);
        PRIMITIVE_DEFAULT_MAP.put(Boolean.TYPE, false);
        PRIMITIVE_DEFAULT_MAP.put(Byte.TYPE, (byte)0);
        PRIMITIVE_DEFAULT_MAP.put(Character.TYPE, Character.valueOf('\u0000'));
        PRIMITIVE_DEFAULT_MAP.put(Short.TYPE, (short)0);
        PRIMITIVE_DEFAULT_MAP.put(Long.TYPE, 0L);
        PRIMITIVE_DEFAULT_MAP.put(Float.TYPE, Float.valueOf(0.0f));
        PRIMITIVE_DEFAULT_MAP.put(Double.TYPE, 0.0);
    }
}

