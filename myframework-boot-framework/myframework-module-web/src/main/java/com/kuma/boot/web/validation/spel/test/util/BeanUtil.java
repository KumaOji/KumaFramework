/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.test.util;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

public class BeanUtil {
    public static <T> String getFieldName(IGetter<T, ?> fn) {
        try {
            Method writeReplace = fn.getClass().getDeclaredMethod("writeReplace", new Class[0]);
            writeReplace.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda)writeReplace.invoke(fn, new Object[0]);
            Object methodName = serializedLambda.getImplMethodName();
            if (((String)methodName).startsWith("get")) {
                methodName = ((String)methodName).substring(3);
            } else if (((String)methodName).startsWith("is")) {
                methodName = ((String)methodName).substring(2);
            }
            methodName = ((String)methodName).length() > 1 ? Character.toLowerCase(((String)methodName).charAt(0)) + ((String)methodName).substring(1) : ((String)methodName).toLowerCase();
            return methodName;
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to get the field name", e);
        }
    }
}

