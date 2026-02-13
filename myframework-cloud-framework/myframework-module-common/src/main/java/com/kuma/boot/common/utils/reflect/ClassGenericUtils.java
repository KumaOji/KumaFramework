/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.kuma.boot.common.utils.reflect;

import com.google.common.collect.Lists;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public final class ClassGenericUtils {
    private ClassGenericUtils() {
    }

    private static List<Type> getGenericInterfaces(Class clazz) {
        Type superType;
        HashSet<Type> typeSet = new HashSet<Type>();
        Object[] types = clazz.getGenericInterfaces();
        if (ArrayUtils.isNotEmpty(types)) {
            typeSet.addAll(Lists.newArrayList((Object[])types));
        }
        if (ObjectUtils.isNotNull(superType = clazz.getGenericSuperclass()) && superType.getClass().isInterface()) {
            typeSet.add(superType);
        }
        return Lists.newArrayList(typeSet);
    }

    public static Class getGenericClass(Class clazz, Class interfaceClass, int index) {
        List<Type> typeList = ClassGenericUtils.getGenericInterfaces(clazz);
        for (Type type : typeList) {
            if (!(type instanceof ParameterizedType) || !interfaceClass.equals(((ParameterizedType)type).getRawType())) continue;
            ParameterizedType p = (ParameterizedType)type;
            return (Class)p.getActualTypeArguments()[index];
        }
        return Object.class;
    }

    public static Class getGenericClass(Collection<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        for (Object object : list) {
            if (!ObjectUtils.isNotNull(object)) continue;
            return object.getClass();
        }
        return null;
    }

    public static Class getGenericSupperClass(Class clazz, int index) {
        Class classType = Object.class;
        Type pageVoParserClass = clazz.getGenericSuperclass();
        if (pageVoParserClass instanceof ParameterizedType) {
            Type[] pageVoClassTypes = ((ParameterizedType)pageVoParserClass).getActualTypeArguments();
            classType = (Class)pageVoClassTypes[index];
        }
        return classType;
    }

    public static Class getGenericSupperClass(Class clazz) {
        return ClassGenericUtils.getGenericSupperClass(clazz, 0);
    }
}

