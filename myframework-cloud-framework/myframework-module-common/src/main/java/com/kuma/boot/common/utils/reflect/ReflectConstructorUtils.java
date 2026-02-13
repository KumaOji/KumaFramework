/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.reflect;

import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.utils.common.ArgUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class ReflectConstructorUtils {
    private ReflectConstructorUtils() {
    }

    public static <T> T newInstance(Constructor<T> constructor, Object ... args) {
        ArgUtils.notNull(constructor, "constructor");
        try {
            return constructor.newInstance(args);
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new BootException(e);
        }
    }
}

