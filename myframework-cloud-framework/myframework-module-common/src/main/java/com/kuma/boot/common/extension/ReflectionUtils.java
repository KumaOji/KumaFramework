/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.extension;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/** 反射工具集 */
@SuppressWarnings("unchecked")
public class ReflectionUtils {

    public static <T> T newProxyInstance(Class<T> clazz, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), handler);
    }

    public static void setField(Object destObj, Field field, Object... argument)
            throws IllegalArgumentException, IllegalAccessException {
        setAccessible(field);
        field.set(destObj, argument);
    }

    public static void setField(Object destObj, String fieldName, Object... argument)
            throws IllegalArgumentException, IllegalAccessException, SecurityException {
        Field field = getDeclaredField(destObj, fieldName);
        setField(destObj, field, argument);
    }

    public static Object getField(Object destObj, Field field)
            throws IllegalArgumentException, IllegalAccessException {
        setAccessible(field);
        return field.get(destObj);
    }

    public static Object getField(Object destObj, String fieldName)
            throws IllegalArgumentException, IllegalAccessException, SecurityException {
        Field field = getDeclaredField(destObj, fieldName);
        return getField(destObj, field);
    }

    public static Field getDeclaredFields(Class<?> clazz, String fieldName)
            throws SecurityException {
        List<Field> fields = getDeclaredFields(clazz);
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    public static Field getDeclaredField(Object destObj, String fieldName)
            throws SecurityException {
        return getDeclaredFields(destObj.getClass(), fieldName);
    }

    public static List<Field> getDeclaredFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> tempClass = clazz;
        while (tempClass != null) {
            fields.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            tempClass = tempClass.getSuperclass();
        }
        return fields;
    }

    public static Object invokeMethod(
            Object object, String methodName, Class<?>[] parameterTypes, Object[] parameters)
            throws SecurityException,
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException {
        Method method = getDeclaredMethod(object.getClass(), methodName, parameterTypes);
        setAccessible(method);
        return method.invoke(object, parameters);
    }

    public static Method getDeclaredMethod(
            Class<?> clazz, String methodName, Class<?>... parameterTypes)
            throws SecurityException {
        List<Method> methodList = getDeclaredMethods(clazz);
        for (Method method : methodList) {
            if (method.getName().equals(methodName)) {
                if (parameterTypes.length == 0 || method.getParameterTypes() == null) {
                    return method;
                }
                if (parameterTypes
                        .getClass()
                        .isAssignableFrom(method.getParameterTypes().getClass())) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 获取实际的泛型类型
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Class<T> getActualTypeArgument(Class<?> clazz) {
        Type type = clazz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return (Class<T>) parameterizedType.getActualTypeArguments()[0];
        }
        throw new RuntimeException();
    }

    public static Method getSetter(Class<?> clazz, Field field, Class<?>... parameterTypes)
            throws SecurityException {
        String fieldName = field.getName();
        String methodName =
                "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return getDeclaredMethod(clazz, methodName, parameterTypes);
    }

    public static Method getGetter(Class<?> clazz, final Field field) throws SecurityException {
        String fieldName = field.getName();
        String methodName =
                "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return getDeclaredMethod(clazz, methodName);
    }

    public static List<Method> getDeclaredMethods(Class<?> clazz) {
        List<Method> methodList = new ArrayList<>();
        Class<?> tempClass = clazz;
        while (tempClass != null) {
            methodList.addAll(Arrays.asList(tempClass.getDeclaredMethods()));
            tempClass = tempClass.getSuperclass();
        }
        return methodList;
    }

    public static <T> Class<T> getSuperClassGenricType(Class<?> clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return (Class<T>) Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0 || !(params[index] instanceof Class)) {
            return (Class<T>) Object.class;
        }
        return (Class<T>) params[index];
    }

    public static void setAccessible(Field field) {
        Class<?> cls = field.getDeclaringClass();
        if ((!isPublic(field) || !isPublic(cls) || isFinal(field)) && !field.canAccess(null)) {
            field.setAccessible(true);
        }
    }

    public static void setAccessible(Method method) {
        Class<?> cls = method.getDeclaringClass();
        if ((!isPublic(method) || !isPublic(cls)) && !method.canAccess(null)) {
            method.setAccessible(true);
        }
    }

    public static boolean isPublic(Class<?> cls) {
        return Modifier.isPublic(cls.getModifiers());
    }

    public static boolean isPublic(Method method) {
        return Modifier.isPublic(method.getModifiers());
    }

    public static boolean isPublic(Field field) {
        return Modifier.isPublic(field.getModifiers());
    }

    public static boolean isProtected(Class<?> cls) {
        return Modifier.isProtected(cls.getModifiers());
    }

    public static boolean isProtected(Method method) {
        return Modifier.isProtected(method.getModifiers());
    }

    public static boolean isProtected(Field field) {
        return Modifier.isProtected(field.getModifiers());
    }

    public static boolean isPrivate(Class<?> cls) {
        return Modifier.isPrivate(cls.getModifiers());
    }

    public static boolean isPrivate(Method method) {
        return Modifier.isPrivate(method.getModifiers());
    }

    public static boolean isPrivate(Field field) {
        return Modifier.isPrivate(field.getModifiers());
    }

    public static boolean isFinal(Class<?> cls) {
        return Modifier.isFinal(cls.getModifiers());
    }

    public static boolean isFinal(Method method) {
        return Modifier.isFinal(method.getModifiers());
    }

    public static boolean isFinal(Field field) {
        return Modifier.isFinal(field.getModifiers());
    }

    public static boolean isStaticMethod(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    public static boolean isPrimitives(Class<?> cls) {
        while (cls.isArray()) {
            cls = cls.getComponentType();
        }
        return isPrimitive(cls);
    }

    public static boolean isPrimitive(Class<?> cls) {
        return cls.isPrimitive()
                || cls == String.class
                || cls == Boolean.class
                || cls == Character.class
                || Number.class.isAssignableFrom(cls)
                || Date.class.isAssignableFrom(cls);
    }

    public static boolean isSetter(Method method) {
        return method.getName().startsWith("set")
                && method.getParameterTypes().length == 1
                && isPublic(method);
    }

    public static boolean isGetter(Method method) {
        return method.getName().startsWith("get")
                && method.getParameterTypes().length == 1
                && isPublic(method);
    }

    public static String getSetterProperty(Method method) {
        return method.getName().length() > 3
                ? method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4)
                : "";
    }

    public static String getGetterProperty(Method method) {
        return method.getName().length() > 3
                ? method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4)
                : "";
    }
}
