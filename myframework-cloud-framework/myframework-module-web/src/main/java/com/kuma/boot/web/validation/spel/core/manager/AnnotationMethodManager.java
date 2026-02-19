/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.core.manager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotationMethodManager {
    private static final ConcurrentHashMap<String, Method> METHOD_CACHE = new ConcurrentHashMap();

    private AnnotationMethodManager() {
    }

    public static Method get(Class<? extends Annotation> clazz, String methodName) {
        return METHOD_CACHE.computeIfAbsent(clazz.toString() + "#" + methodName, s -> {
            try {
                return clazz.getMethod(methodName, new Class[0]);
            }
            catch (NoSuchMethodException e) {
                return null;
            }
        });
    }
}

