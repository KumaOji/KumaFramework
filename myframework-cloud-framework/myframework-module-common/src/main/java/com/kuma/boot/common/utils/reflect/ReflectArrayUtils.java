/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.reflect;

public final class ReflectArrayUtils {
    private ReflectArrayUtils() {
    }

    public static Class getComponentType(Object[] objects) {
        Class<?> arrayClass = objects.getClass();
        return arrayClass.getComponentType();
    }
}

