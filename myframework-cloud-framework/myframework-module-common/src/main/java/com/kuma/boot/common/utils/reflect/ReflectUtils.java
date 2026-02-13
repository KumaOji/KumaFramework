/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.ReflectUtil
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.common.utils.reflect;

import cn.hutool.core.util.ReflectUtil;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ReflectUtils
extends ReflectUtil {
    private static final String SETTER_PREFIX = "set";
    private static final String GETTER_PREFIX = "get";

    public static <E> E invokeGetter(Object obj, String propertyName) {
        Object object = obj;
        for (String name : StringUtils.split(propertyName, ".")) {
            String getterMethodName = GETTER_PREFIX + org.springframework.util.StringUtils.capitalize((String)name);
            object = ReflectUtils.invoke((Object)object, (String)getterMethodName, (Object[])new Object[0]);
        }
        return (E)object;
    }

    public static <E> void invokeSetter(Object obj, String propertyName, E value) {
        Object object = obj;
        String[] names = StringUtils.split(propertyName, ".");
        for (int i = 0; i < names.length; ++i) {
            if (i < names.length - 1) {
                String getterMethodName = GETTER_PREFIX + org.springframework.util.StringUtils.capitalize((String)names[i]);
                object = ReflectUtils.invoke((Object)object, (String)getterMethodName, (Object[])new Object[0]);
                continue;
            }
            String setterMethodName = SETTER_PREFIX + org.springframework.util.StringUtils.capitalize((String)names[i]);
            Method method = ReflectUtils.getMethodByName(object.getClass(), (String)setterMethodName);
            ReflectUtils.invoke((Object)object, (Method)method, (Object[])new Object[]{value});
        }
    }

    public static Object invokeDefaultMethod(Object proxy, Method method, Object[] args) {
        try {
            Constructor constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, Integer.TYPE);
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            Class<?> declaringClass = method.getDeclaringClass();
            return ((MethodHandles.Lookup)constructor.newInstance(declaringClass, 2)).unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
        }
        catch (Throwable e) {
            throw new RuntimeException("\u8c03\u7528default\u65b9\u6cd5\u51fa\u9519", e);
        }
    }

    public static boolean isDefaultMethod(Method method) {
        return (method.getModifiers() & 0x409) == 1 && method.getDeclaringClass().isInterface();
    }
}

