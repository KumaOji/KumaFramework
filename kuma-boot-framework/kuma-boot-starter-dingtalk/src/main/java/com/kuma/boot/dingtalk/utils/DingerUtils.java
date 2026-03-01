/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.utils;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class DingerUtils {
    private DingerUtils() {
    }

    public static byte[] base64ToByteArray(String key) {
        return Base64.getDecoder().decode(key);
    }

    public static String byteArrayToBase64(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes));
    }

    public static void close(Closeable x) {
        if (x == null) {
            return;
        }
        try {
            x.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    public static boolean isNotEmpty(String str) {
        return !DingerUtils.isEmpty(str);
    }

    public static String replaceHeadTailLineBreak(String str) {
        String preRegex = "^(\\s*| | |\t)(\\S*)";
        String suffixRegex = "(\\S*)(\\s*| | |\t)$";
        String regex = "[\t| ]{2,}";
        return str.replaceAll(preRegex, "$2").replaceAll(suffixRegex, "$1").replaceAll(regex, " ");
    }

    public static String classPackageName(String className) {
        return className.substring(0, className.lastIndexOf("."));
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    public static int[] methodParamsGenericType(Method method, Class<?> clazz) {
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        int length = genericParameterTypes.length;
        int[] arr = new int[length];
        for (int i = 0; i < length; ++i) {
            Type actualTypeArgument;
            ParameterizedType parameterizedType;
            Type type = genericParameterTypes[i];
            arr[i] = -1;
            if (!ParameterizedType.class.isInstance(type) || (parameterizedType = (ParameterizedType)type).getRawType() != List.class || (actualTypeArgument = parameterizedType.getActualTypeArguments()[0]) != clazz) continue;
            arr[i] = i;
        }
        return Arrays.stream(arr).filter(e -> e > -1).toArray();
    }

    public static int[] methodParamsType(Method method, Class<?> clazz) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        int length = parameterTypes.length;
        int[] arr = new int[length];
        for (int i = 0; i < length; ++i) {
            Class<?> parameterType = parameterTypes[i];
            arr[i] = parameterType.getName().equals(clazz.getName()) ? i : -1;
        }
        return Arrays.stream(arr).filter(e -> e > -1).toArray();
    }
}

