/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.kuma.boot.common.utils.reflect;

import com.google.common.collect.Lists;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.support.handler.Handler;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.reflect.ClassUtils;
import com.kuma.boot.common.utils.reflect.TypeUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public final class ReflectMethodUtils {
    private static final List<String> IGNORE_METHOD_LIST;

    private ReflectMethodUtils() {
    }

    public static List<String> getIgnoreMethodList() {
        return IGNORE_METHOD_LIST;
    }

    public static boolean isIgnoreMethod(String methodName) {
        return ReflectMethodUtils.getIgnoreMethodList().contains(methodName);
    }

    public static List<String> getParamTypeNames(Method method) {
        ArgUtils.notNull(method, "method");
        Class<?>[] paramTypes = method.getParameterTypes();
        return ArrayUtils.toList(paramTypes, new Handler<Class<?>, String>(){

            @Override
            public String handle(Class<?> aClass) {
                return aClass.getName();
            }
        });
    }

    public static List<String> getParamNames(Method method) {
        ArgUtils.notNull(method, "method");
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        return ReflectMethodUtils.getParamNames(parameterAnnotations);
    }

    public static List<String> getParamNames(Annotation[][] parameterAnnotations) {
        if (ArrayUtils.isEmpty((Object[])parameterAnnotations)) {
            return Collections.emptyList();
        }
        int paramSize = parameterAnnotations.length;
        ArrayList resultList = Lists.newArrayList();
        for (int i = 0; i < paramSize; ++i) {
            Annotation[] annotations = parameterAnnotations[i];
            String paramName = ReflectMethodUtils.getParamName(i, annotations);
            resultList.add(paramName);
        }
        return resultList;
    }

    private static String getParamName(int index, Annotation[] annotations) {
        String defaultName = "arg" + index;
        if (ArrayUtils.isEmpty(annotations)) {
            return defaultName;
        }
        for (Annotation annotation : annotations) {
        }
        return defaultName;
    }

    public static Class getReturnGenericType(Method method, int index) {
        Type returnType = method.getGenericReturnType();
        if (returnType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType)returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            return (Class)typeArguments[index];
        }
        return null;
    }

    public static Class getParamGenericType(Method method, int paramIndex, int genericIndex) {
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        Type genericParameterType = genericParameterTypes[paramIndex];
        if (genericParameterType instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType)genericParameterType;
            Type[] parameterArgTypes = aType.getActualTypeArguments();
            return (Class)parameterArgTypes[genericIndex];
        }
        return null;
    }

    public static Optional<Method> getMethodOptional(Class tClass, Class<? extends Annotation> annotationClass) {
        Object[] methods = tClass.getMethods();
        if (ArrayUtils.isEmpty(methods)) {
            return Optional.empty();
        }
        for (Object method : methods) {
            if (!((AccessibleObject)method).isAnnotationPresent(annotationClass)) continue;
            return Optional.of(method);
        }
        return Optional.empty();
    }

    public static Object invoke(Object instance, Method method, Object ... args) {
        ArgUtils.notNull(method, "method");
        try {
            return method.invoke(instance, args);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new BootException(e);
        }
    }

    public static Object invoke(Object instance, String methodName, Object ... args) {
        ArgUtils.notEmpty(methodName, "methodName");
        try {
            if (ArrayUtils.isEmpty(args)) {
                return ReflectMethodUtils.invokeNoArgsMethod(instance, methodName);
            }
            Class<?> clazz = instance.getClass();
            Class[] paramTypes = new Class[args.length];
            for (int i = 0; i < args.length; ++i) {
                Object param = args[i];
                paramTypes[i] = param.getClass();
            }
            Method method = ClassUtils.getMethod(clazz, methodName, paramTypes);
            return method.invoke(instance, args);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new BootException(e);
        }
    }

    public static Object invokeNoArgsMethod(Object instance, Method method) {
        if (ObjectUtils.isNull(method)) {
            return null;
        }
        String methodName = method.getName();
        Object[] paramTypes = method.getParameterTypes();
        if (ArrayUtils.isNotEmpty(paramTypes)) {
            throw new BootException(methodName + " must be has no params.");
        }
        return ReflectMethodUtils.invoke(instance, method, new Object[0]);
    }

    public static Object invokeNoArgsMethod(Object instance, String methodName) {
        ArgUtils.notNull(instance, "instance");
        Class<?> clazz = instance.getClass();
        Method method = ClassUtils.getMethod(clazz, methodName);
        return ReflectMethodUtils.invokeNoArgsMethod(instance, method);
    }

    public static Object invokeFactoryMethod(Class clazz, Method factoryMethod) {
        ArgUtils.notNull(clazz, "clazz");
        ArgUtils.notNull(factoryMethod, "factoryMethod");
        String methodName = factoryMethod.getName();
        Object[] paramTypes = factoryMethod.getParameterTypes();
        if (ArrayUtils.isNotEmpty(paramTypes)) {
            throw new BootException(methodName + " must be has no params.");
        }
        if (!Modifier.isStatic(factoryMethod.getModifiers())) {
            throw new BootException(methodName + " must be static.");
        }
        Class<?> returnType = factoryMethod.getReturnType();
        if (!returnType.isAssignableFrom(clazz)) {
            throw new BootException(methodName + " must be return " + returnType.getName());
        }
        return ReflectMethodUtils.invoke(null, factoryMethod, new Object[0]);
    }

    public static Class getGenericReturnParamType(Method method, int paramIndex) {
        ArgUtils.notNull(method, "method");
        ArgUtils.notNegative(paramIndex, "paramIndex");
        Type returnType = method.getGenericReturnType();
        if (ObjectUtils.isNull(returnType)) {
            return null;
        }
        return TypeUtils.getGenericParamType(returnType, paramIndex);
    }

    public static void invokeSetterMethod(Object instance, String propertyName, Object value) {
        ArgUtils.notNull(instance, "instance");
        ArgUtils.notNull(propertyName, "propertyName");
        if (ObjectUtils.isNull(value)) {
            return;
        }
        Class<?> clazz = instance.getClass();
        String setMethodName = ReflectMethodUtils.buildSetMethodName(propertyName);
        Class<?> paramType = value.getClass();
        try {
            Method method = clazz.getMethod(setMethodName, paramType);
            method.invoke(instance, value);
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new BootException(e);
        }
    }

    public static Object invokeGetterMethod(Object instance, String fieldName, Class fieldType) {
        ArgUtils.notNull(instance, "instance");
        ArgUtils.notNull(fieldType, "fieldType");
        ArgUtils.notEmpty(fieldName, "fieldName");
        Class<?> clazz = instance.getClass();
        String getMethodName = ReflectMethodUtils.buildGetMethodName(fieldType, fieldName);
        try {
            Method method = clazz.getMethod(getMethodName, new Class[0]);
            return method.invoke(instance, new Object[0]);
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new BootException(e);
        }
    }

    public static Object invokeGetterMethod(Object instance, String fieldName) {
        return ReflectMethodUtils.invokeGetterMethod(instance, fieldName, String.class);
    }

    public static Object invokeGetterMethod(Object instance, Field field) {
        Class<?> fieldType = field.getType();
        String fieldName = field.getName();
        return ReflectMethodUtils.invokeGetterMethod(instance, fieldName, fieldType);
    }

    public static String buildSetMethodName(String propertyName) {
        ArgUtils.notEmpty(propertyName, "propertyName");
        return "set" + StringUtils.firstToUpperCase(propertyName);
    }

    public static String buildGetMethodName(Class fieldType, String propertyName) {
        ArgUtils.notNull(fieldType, "fieldType");
        ArgUtils.notEmpty(propertyName, "propertyName");
        if (Boolean.TYPE.equals(fieldType)) {
            return "is" + StringUtils.firstToUpperCase(propertyName);
        }
        return "get" + StringUtils.firstToUpperCase(propertyName);
    }

    public static String buildGetMethodName(String propertyName) {
        return ReflectMethodUtils.buildGetMethodName(String.class, propertyName);
    }

    static {
        HashSet<String> methodNameSet = new HashSet<String>(64);
        for (Method method : Object.class.getMethods()) {
            methodNameSet.add(method.getName());
        }
        for (Method method : Class.class.getMethods()) {
            methodNameSet.add(method.getName());
        }
        IGNORE_METHOD_LIST = new ArrayList<String>(methodNameSet);
    }
}

