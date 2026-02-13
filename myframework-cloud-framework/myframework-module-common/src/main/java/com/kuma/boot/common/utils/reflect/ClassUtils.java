/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  org.jspecify.annotations.Nullable
 *  org.springframework.core.BridgeMethodResolver
 *  org.springframework.core.DefaultParameterNameDiscoverer
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.ParameterNameDiscoverer
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.core.annotation.SynthesizingMethodParameter
 *  org.springframework.util.ClassUtils
 *  org.springframework.web.method.HandlerMethod
 */
package com.kuma.boot.common.utils.reflect;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.collection.MapUtils;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.reflect.PrimitiveUtils;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.web.method.HandlerMethod;

public final class ClassUtils
extends org.springframework.util.ClassUtils {
    public static final String SERIAL_VERSION_UID = "serialVersionUID";
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private ClassUtils() {
    }

    public static String getClassVar(String className) {
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }

    public static void notExistThrow(Class<?> clazz, String errorMsg) {
        try {
            Class.forName(clazz.getCanonicalName());
        }
        catch (ClassNotFoundException e) {
            throw new ClassCastException(errorMsg);
        }
    }

    public static boolean isExist(String className) {
        try {
            Class.forName(className);
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static List<Field> getAllFieldList(Class<?> clazz) {
        ArrayList allFieldList = new ArrayList();
        for (Class<?> tempClass = clazz; tempClass != null; tempClass = tempClass.getSuperclass()) {
            allFieldList.addAll(Lists.newArrayList((Object[])tempClass.getDeclaredFields()));
        }
        ArrayList resultList = Lists.newArrayList();
        for (Field field : allFieldList) {
            String fieldName = field.getName();
            if (SERIAL_VERSION_UID.equals(fieldName)) continue;
            field.setAccessible(true);
            resultList.add(field);
        }
        return resultList;
    }

    public static List<Field> getModifyableFieldList(Class clazz) {
        List<Field> allFieldList = ClassUtils.getAllFieldList(clazz);
        if (CollectionUtils.isEmpty(allFieldList)) {
            return allFieldList;
        }
        return CollectionUtils.filterList(allFieldList, field -> Modifier.isFinal(field.getModifiers()));
    }

    public static Map<String, Field> getAllFieldMap(Class clazz) {
        List<Field> fieldList = ClassUtils.getAllFieldList(clazz);
        return MapUtils.toMap(fieldList, Field::getName);
    }

    @Deprecated
    public static Map<String, Object> beanToMap(Object bean) {
        try {
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
            List<Field> fieldList = ClassUtils.getAllFieldList(bean.getClass());
            for (Field field : fieldList) {
                String fieldName = field.getName();
                Object fieldValue = field.get(bean);
                map.put(fieldName, fieldValue);
            }
            return map;
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
        catch (NoSuchMethodException | InvocationTargetException e) {
            LogUtils.error(e);
            return null;
        }
    }

    public static List<Method> getAllFieldsReadMethods(Class clazz) throws IntrospectionException {
        List<Field> fieldList = ClassUtils.getAllFieldList(clazz);
        if (CollectionUtils.isEmpty(fieldList)) {
            return Collections.emptyList();
        }
        ArrayList<Method> methods = new ArrayList<Method>();
        for (Field field : fieldList) {
            PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
            Method getMethod = pd.getReadMethod();
            methods.add(getMethod);
        }
        return methods;
    }

    public static ClassLoader currentClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static Class getClass(String className) {
        ArgUtils.notEmpty(className, "className");
        try {
            return ClassUtils.currentClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e) {
            throw new BootException(e);
        }
    }

    public static Method getMethod(Class clazz, String methodName, Class ... paramTypes) {
        ArgUtils.notNull(clazz, "clazz");
        ArgUtils.notEmpty(methodName, "methodName");
        try {
            return clazz.getMethod(methodName, paramTypes);
        }
        catch (NoSuchMethodException e) {
            throw new BootException(e);
        }
    }

    public static Method getMethod(Class<?> clazz, String methodName) {
        Method[] methods;
        ArgUtils.notNull(clazz, "clazz");
        ArgUtils.notEmpty(methodName, "methodName");
        for (Method method : methods = clazz.getMethods()) {
            if (!method.getName().equals(methodName)) continue;
            return method;
        }
        throw new BootException("\u5bf9\u5e94\u65b9\u6cd5\u4e0d\u5b58\u5728!");
    }

    public static Constructor getConstructor(Class clazz, Class ... paramTypes) {
        ArgUtils.notNull(clazz, "clazz");
        try {
            return clazz.getConstructor(paramTypes);
        }
        catch (NoSuchMethodException e) {
            throw new BootException(e);
        }
    }

    public static List<Method> getMethodList(Class tClass) {
        ArgUtils.notNull(tClass, "tClass");
        Method[] methods = tClass.getMethods();
        return ArrayUtils.toList(methods);
    }

    public static List<Method> getDeclaredMethodList(Class tClass) {
        ArgUtils.notNull(tClass, "tClass");
        Method[] methods = tClass.getDeclaredMethods();
        return ArrayUtils.toList(methods);
    }

    public static List<Class> getAllSuperClass(Class clazz) {
        ArgUtils.notNull(clazz, "clazz");
        HashSet classSet = Sets.newHashSet();
        for (Class tempClass = clazz.getSuperclass(); tempClass != null; tempClass = tempClass.getSuperclass()) {
            classSet.add(tempClass);
        }
        return Lists.newArrayList((Iterable)classSet);
    }

    public static List<Class> getAllInterfaces(Class clazz) {
        ArgUtils.notNull(clazz, "clazz");
        HashSet classSet = Sets.newHashSet();
        Object[] interfaces = clazz.getInterfaces();
        if (ArrayUtils.isNotEmpty(interfaces)) {
            classSet.addAll(ArrayUtils.toList(interfaces));
            for (Object interfaceClass : interfaces) {
                List<Class> classList = ClassUtils.getAllInterfaces((Class)interfaceClass);
                if (!CollectionUtils.isNotEmpty(classList)) continue;
                classSet.addAll(classList);
            }
        }
        return Lists.newArrayList((Iterable)classSet);
    }

    public static List<Class> getAllInterfacesAndSuperClass(Class clazz) {
        ArgUtils.notNull(clazz, "clazz");
        HashSet classSet = Sets.newHashSet();
        classSet.addAll(ClassUtils.getAllInterfaces(clazz));
        classSet.addAll(ClassUtils.getAllSuperClass(clazz));
        return Lists.newArrayList((Iterable)classSet);
    }

    public static boolean isAssignable(Class<?> sourceType, Class<?> targetType) {
        if (ObjectUtils.isNull(sourceType) || ObjectUtils.isNull(targetType)) {
            return false;
        }
        if (sourceType.isAssignableFrom(targetType)) {
            return true;
        }
        if (sourceType.isPrimitive()) {
            Class<?> resolvedPrimitive = PrimitiveUtils.getPrimitiveType(targetType);
            return sourceType == resolvedPrimitive;
        }
        Class<?> resolvedPrimitive = PrimitiveUtils.getPrimitiveType(targetType);
        return resolvedPrimitive != null && sourceType.isAssignableFrom(resolvedPrimitive);
    }

    public static <A extends Annotation> @Nullable A getAnnotation(Method method, Class<A> annotationType) {
        Class<?> targetClass = method.getDeclaringClass();
        Method specificMethod = ClassUtils.getMostSpecificMethod((Method)method, targetClass);
        Annotation annotation = AnnotatedElementUtils.findMergedAnnotation((AnnotatedElement)(specificMethod = BridgeMethodResolver.findBridgedMethod((Method)specificMethod)), annotationType);
        if (null != annotation) {
            return (A)annotation;
        }
        return (A)AnnotatedElementUtils.findMergedAnnotation(specificMethod.getDeclaringClass(), annotationType);
    }

    public static <A extends Annotation> boolean isAnnotated(Method method, Class<A> annotationType) {
        boolean isMethodAnnotated = AnnotatedElementUtils.isAnnotated((AnnotatedElement)method, annotationType);
        if (isMethodAnnotated) {
            return true;
        }
        Class<?> targetClass = method.getDeclaringClass();
        return AnnotatedElementUtils.isAnnotated(targetClass, annotationType);
    }

    public static MethodParameter getMethodParameter(Constructor<?> constructor, int parameterIndex) {
        SynthesizingMethodParameter methodParameter = new SynthesizingMethodParameter(constructor, parameterIndex);
        methodParameter.initParameterNameDiscovery(PARAMETER_NAME_DISCOVERER);
        return methodParameter;
    }

    public static MethodParameter getMethodParameter(Method method, int parameterIndex) {
        SynthesizingMethodParameter methodParameter = new SynthesizingMethodParameter(method, parameterIndex);
        methodParameter.initParameterNameDiscovery(PARAMETER_NAME_DISCOVERER);
        return methodParameter;
    }

    public static <A extends Annotation> @Nullable A getAnnotation(HandlerMethod handlerMethod, Class<A> annotationType) {
        Annotation annotation = handlerMethod.getMethodAnnotation(annotationType);
        if (null != annotation) {
            return (A)annotation;
        }
        Class beanType = handlerMethod.getBeanType();
        return (A)AnnotatedElementUtils.findMergedAnnotation((AnnotatedElement)beanType, annotationType);
    }
}

