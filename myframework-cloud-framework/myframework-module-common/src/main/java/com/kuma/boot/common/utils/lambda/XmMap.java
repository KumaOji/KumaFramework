/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.lambda;

import com.kuma.boot.common.utils.lambda.SFunction;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class XmMap<K, V>
extends HashMap<K, V> {
    private static final long serialVersionUID = -5260635176470805065L;
    private static Map<Class, SerializedLambda> CLASS_LAMDBA_CACHE = new ConcurrentHashMap<Class, SerializedLambda>();

    public <T> boolean containsKey(SFunction<T, ?> key) {
        return super.containsKey(XmMap.getField(key));
    }

    public <T> V remove(SFunction<T, ?> key) {
        return super.remove(key);
    }

    @Override
    public <T> V putIfAbsent(SFunction<T, ?> key, V v) {
        return super.putIfAbsent(XmMap.getField(key), v);
    }

    public <T> V get(SFunction<T, ?> key) {
        return super.get(XmMap.getField(key));
    }

    @Override
    public <T> V put(SFunction<T, ?> key, V v) {
        return super.put(XmMap.getField(key), v);
    }

    public static <T> String getField(SFunction<T, ?> fn) {
        SerializedLambda serializedLambda = XmMap.getSerializedLambda(fn);
        String implMethodName = serializedLambda.getImplMethodName();
        String prefix = null;
        if (implMethodName.startsWith("get")) {
            prefix = "get";
        } else if (implMethodName.startsWith("is")) {
            prefix = "is";
        }
        if (prefix == null) {
            throw new RuntimeException("get\u65b9\u6cd5\u540d\u79f0: " + implMethodName + ", \u4e0d\u7b26\u5408java bean\u89c4\u8303");
        }
        return XmMap.toLowerCaseFirstOne(implMethodName.replace(prefix, ""));
    }

    private static SerializedLambda getSerializedLambda(Serializable fn) {
        Class<?> fnClass = fn.getClass();
        SerializedLambda lambda = CLASS_LAMDBA_CACHE.get(fnClass);
        if (lambda == null) {
            try {
                Method method = fnClass.getDeclaredMethod("writeReplace", new Class[0]);
                boolean isAccessible = method.isAccessible();
                method.setAccessible(Boolean.TRUE);
                lambda = (SerializedLambda)method.invoke((Object)fn, new Object[0]);
                method.setAccessible(isAccessible);
                CLASS_LAMDBA_CACHE.put(fnClass, lambda);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lambda;
    }

    private static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        }
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
}

