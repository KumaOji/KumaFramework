/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.bean.BeanUtil
 *  cn.hutool.core.bean.copier.CopyOptions
 *  cn.hutool.core.util.ArrayUtil
 *  cn.hutool.core.util.ReflectUtil
 *  jakarta.annotation.Nullable
 *  org.springframework.beans.BeansException
 *  org.springframework.cglib.core.CodeGenerationException
 *  org.springframework.core.convert.Property
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.util.ReflectionUtils
 */
package com.kuma.boot.common.utils.reflect;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.annotation.Nullable;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.BeansException;
import org.springframework.cglib.core.CodeGenerationException;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;

public class ReflectionUtils
extends org.springframework.util.ReflectionUtils {
    public static PropertyDescriptor[] getBeanGetters(Class<?> type) {
        return ReflectionUtils.getPropertyDescriptors(type, true, false);
    }

    public static PropertyDescriptor[] getBeanSetters(Class<?> type) {
        return ReflectionUtils.getPropertyDescriptors(type, false, true);
    }

    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> type, boolean read, boolean write) {
        try {
            PropertyDescriptor[] all = BeanUtil.getPropertyDescriptors(type);
            if (read && write) {
                return all;
            }
            ArrayList<PropertyDescriptor> properties = new ArrayList<PropertyDescriptor>(all.length);
            for (PropertyDescriptor pd : all) {
                if (read && pd.getReadMethod() != null) {
                    properties.add(pd);
                    continue;
                }
                if (!write || pd.getWriteMethod() == null) continue;
                properties.add(pd);
            }
            return properties.toArray(new PropertyDescriptor[0]);
        }
        catch (BeansException ex) {
            throw new CodeGenerationException((Throwable)ex);
        }
    }

    @Nullable
    public static Property getProperty(Class<?> propertyType, String propertyName) {
        PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(propertyType, (String)propertyName);
        if (propertyDescriptor == null) {
            return null;
        }
        return ReflectionUtils.getProperty(propertyType, propertyDescriptor, propertyName);
    }

    public static Property getProperty(Class<?> propertyType, PropertyDescriptor propertyDescriptor, String propertyName) {
        Method readMethod = propertyDescriptor.getReadMethod();
        Method writeMethod = propertyDescriptor.getWriteMethod();
        return new Property(propertyType, readMethod, writeMethod, propertyName);
    }

    @Nullable
    public static TypeDescriptor getTypeDescriptor(Class<?> propertyType, String propertyName) {
        Property property = ReflectionUtils.getProperty(propertyType, propertyName);
        if (property == null) {
            return null;
        }
        return new TypeDescriptor(property);
    }

    public static TypeDescriptor getTypeDescriptor(Class<?> propertyType, PropertyDescriptor propertyDescriptor, String propertyName) {
        Method readMethod = propertyDescriptor.getReadMethod();
        Method writeMethod = propertyDescriptor.getWriteMethod();
        Property property = new Property(propertyType, readMethod, writeMethod, propertyName);
        return new TypeDescriptor(property);
    }

    @Nullable
    public static Field getField(Class<?> clazz, String fieldName) {
        while (clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            }
            catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    @Nullable
    public static <T extends Annotation> T getAnnotation(Class<?> clazz, String fieldName, Class<T> annotationClass) {
        Field field = ReflectionUtils.getField(clazz, fieldName);
        if (field == null) {
            return null;
        }
        return field.getAnnotation(annotationClass);
    }

    public static void setField(Field field, @Nullable Object target, @Nullable Object value) {
        ReflectionUtils.makeAccessible((Field)field);
        org.springframework.util.ReflectionUtils.setField((Field)field, (Object)target, (Object)value);
    }

    @Nullable
    public static Object getField(Field field, @Nullable Object target) {
        ReflectionUtils.makeAccessible((Field)field);
        return org.springframework.util.ReflectionUtils.getField((Field)field, (Object)target);
    }

    @Nullable
    public static Object getField(String fieldName, @Nullable Object target) {
        if (target == null) {
            return null;
        }
        Class<?> targetClass = target.getClass();
        Field field = ReflectionUtils.getField(targetClass, fieldName);
        if (field == null) {
            throw new IllegalArgumentException(fieldName + " not in" + String.valueOf(targetClass));
        }
        return ReflectionUtils.getField(field, target);
    }

    @Nullable
    public static Object invokeMethod(Method method, @Nullable Object target) {
        return ReflectionUtils.invokeMethod(method, target, new Object[0]);
    }

    @Nullable
    public static Object invokeMethod(Method method, @Nullable Object target, Object ... args) {
        ReflectionUtils.makeAccessible((Method)method);
        return org.springframework.util.ReflectionUtils.invokeMethod((Method)method, (Object)target, (Object[])args);
    }

    private ReflectionUtils() {
    }

    public static Class<?> classForName(String type) {
        try {
            return Class.forName(type);
        }
        catch (Exception exp) {
            throw new BaseException(exp.getMessage());
        }
    }

    public static Class<?> tryClassForName(String type) {
        try {
            return Class.forName(type);
        }
        catch (Exception exp) {
            return null;
        }
    }

    public static Method findMethod(Class<?> cls, String methodName) {
        Method find = null;
        while (cls != null) {
            Method[][] methodArrayArray = new Method[][]{cls.getMethods(), cls.getDeclaredMethods()};
            int n = methodArrayArray.length;
            block1: for (int i = 0; i < n; ++i) {
                Method[] methods;
                for (Method method : methods = methodArrayArray[i]) {
                    if (!method.getName().equalsIgnoreCase(methodName)) continue;
                    find = method;
                    continue block1;
                }
            }
            cls = cls.getSuperclass();
        }
        return find;
    }

    public static Object findEnumObjByName(Class<?> cls, String methodName, String name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ?[] objects = cls.getEnumConstants();
        Method method = cls.getMethod(methodName, new Class[0]);
        for (Object object : objects) {
            Object invoke = method.invoke(object, new Object[0]);
            if (!invoke.equals(name)) continue;
            return object;
        }
        return null;
    }

    public static Method findMethod0(Class<?> cls, String methodName, Class<?> ... argsTypes) throws NoSuchMethodException, SecurityException {
        Method find = null;
        if (cls != null) {
            find = cls.getMethod(methodName, argsTypes);
        }
        return find;
    }

    public static <T> T tryCallMethod(Object obj, String methodName, Object[] param, T defaultValue) {
        try {
            Method method;
            if (obj != null && (method = ReflectionUtils.findMethod(obj.getClass(), methodName)) != null) {
                if (!method.canAccess(obj)) {
                    method.setAccessible(true);
                }
                return (T)method.invoke(obj, param);
            }
            return defaultValue;
        }
        catch (Exception exp) {
            LogUtils.error(exp);
            return defaultValue;
        }
    }

    public static Object callMethod(Object obj, String methodName, Object[] param) {
        try {
            Method find = ReflectionUtils.findMethod(obj.getClass(), methodName);
            if (find != null) {
                return find.invoke(obj, param);
            }
            throw new Exception("\u672a\u627e\u5230\u65b9\u6cd5" + StringUtils.nullToEmpty((CharSequence)methodName));
        }
        catch (Exception exp) {
            LogUtils.error(exp);
            throw new BaseException(exp.getMessage());
        }
    }

    public static Object callMethod(Class<?> clazz, String methodName, Object[] params) {
        try {
            Method find = ReflectionUtils.findMethod(clazz, methodName);
            if (find != null) {
                return find.invoke(null, params);
            }
            throw new Exception("\u672a\u627e\u5230\u65b9\u6cd5" + StringUtils.nullToEmpty((CharSequence)methodName));
        }
        catch (Exception exp) {
            LogUtils.error(exp);
            throw new BaseException(exp.getMessage());
        }
    }

    public static Object callMethodWithParams(Class<?> clazz, String methodName, Object[] params, Class<?> ... paramTypes) {
        try {
            Method find = ReflectionUtils.findMethod0(clazz, methodName, paramTypes);
            if (find != null) {
                return find.invoke(null, params);
            }
            throw new Exception("\u672a\u627e\u5230\u65b9\u6cd5" + StringUtils.nullToEmpty((CharSequence)methodName));
        }
        catch (Exception exp) {
            LogUtils.error(exp);
            throw new BaseException(exp.getMessage());
        }
    }

    public static Object callMethodWithParams(Object object, String methodName, Object[] params, Class<?> ... paramTypes) {
        try {
            Method find = ReflectionUtils.findMethod0(object.getClass(), methodName, paramTypes);
            if (find != null) {
                return find.invoke(object, params);
            }
            throw new Exception("\u672a\u627e\u5230\u65b9\u6cd5" + StringUtils.nullToEmpty((CharSequence)methodName));
        }
        catch (Exception exp) {
            LogUtils.error(exp);
            throw new BaseException(exp.getMessage());
        }
    }

    public static Field findField(Class<?> cls, String name) {
        Field find = null;
        while (cls != null) {
            Field[][] fieldArrayArray = new Field[][]{cls.getFields(), cls.getDeclaredFields()};
            int n = fieldArrayArray.length;
            for (int i = 0; i < n; ++i) {
                Field[] fields;
                for (Field field : fields = fieldArrayArray[i]) {
                    if (!field.getName().equalsIgnoreCase(name)) continue;
                    find = field;
                    return find;
                }
            }
            cls = cls.getSuperclass();
        }
        return find;
    }

    public static RecordComponent findRecord(Class<?> cls, String name) {
        RecordComponent find = null;
        while (cls != null) {
            RecordComponent[] recordComponents = cls.getRecordComponents();
            if (recordComponents.length != 0) {
                for (RecordComponent recordComponent : recordComponents) {
                    if (!recordComponent.getName().equalsIgnoreCase(name)) continue;
                    find = recordComponent;
                    return find;
                }
            }
            cls = cls.getSuperclass();
        }
        return find;
    }

    public static <T> T getFieldValue(Object obj, String name) {
        try {
            Field find = ReflectionUtils.findField(obj.getClass(), name);
            if (find != null) {
                if (!find.canAccess(obj)) {
                    find.setAccessible(true);
                }
                return (T)find.get(obj);
            }
            throw new Exception("\u672a\u627e\u5230\u5b57\u6bb5" + StringUtils.nullToEmpty((CharSequence)name));
        }
        catch (Exception e) {
            LogUtils.error(e);
            throw new BaseException(e.getMessage());
        }
    }

    public static <T> T tryGetFieldValue(Object obj, String name, T defaultValue) {
        try {
            Field find;
            if (obj != null && (find = ReflectionUtils.findField(obj.getClass(), name)) != null) {
                if (!find.canAccess(obj)) {
                    find.setAccessible(true);
                }
                return (T)find.get(obj);
            }
            return defaultValue;
        }
        catch (Exception exp) {
            return defaultValue;
        }
    }

    public static <T> T tryGetStaticFieldValue(String cls, String name, T defaultValue) {
        try {
            return ReflectionUtils.tryGetStaticFieldValue(Class.forName(cls), name, defaultValue);
        }
        catch (Exception exp) {
            LogUtils.error(exp);
            return defaultValue;
        }
    }

    public static <T> T tryGetStaticFieldValue(Class<?> cls, String name, T defaultValue) {
        try {
            Field find;
            if (cls != null && (find = ReflectionUtils.findField(cls, name)) != null) {
                if (!find.canAccess(null)) {
                    find.setAccessible(true);
                }
                return (T)find.get(cls);
            }
            return defaultValue;
        }
        catch (Exception exp) {
            LogUtils.error(exp);
            return defaultValue;
        }
    }

    public static void setFieldValue(Field field, Object obj, Object value) {
        try {
            if (!field.canAccess(obj)) {
                field.setAccessible(true);
            }
            field.set(obj, value);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static <T, DTO> T copyPropertiesIfRecord(T t, DTO dto) {
        if (dto.getClass().isRecord()) {
            Field[] fields;
            for (Field field : fields = dto.getClass().getDeclaredFields()) {
                if ("serialVersionUID".equals(field.getName())) continue;
                T value = ReflectionUtils.tryGetValue(dto, field.getName());
                Field field1 = ReflectionUtils.findField(t.getClass(), field.getName());
                if (!Objects.nonNull(field1) || !Objects.nonNull(value)) continue;
                ReflectionUtils.setFieldValue(field1, t, value);
            }
        } else {
            BeanUtil.copyProperties(dto, t, (CopyOptions)CopyOptions.create().setIgnoreNullValue(true));
        }
        return t;
    }

    public static <T, VO> VO copyPropertiesIfRecord(Class<VO> clazz, T t) {
        Object vo;
        if (clazz.isRecord()) {
            Field[] fields = clazz.getDeclaredFields();
            ArrayList<T> params = new ArrayList<T>();
            ArrayList<Field> fieldList = new ArrayList<Field>();
            for (Field field : fields) {
                if ("serialVersionUID".equals(field.getName())) continue;
                T value = ReflectionUtils.tryGetValue(t, field.getName());
                params.add(value);
                fieldList.add(field);
            }
            vo = ReflectionUtils.newInstance(fieldList, clazz, params.toArray());
        } else {
            vo = ReflectUtil.newInstanceIfPossible(clazz);
            BeanUtil.copyProperties(t, (Object)vo, (CopyOptions)CopyOptions.create().setIgnoreNullValue(true));
        }
        return (VO)vo;
    }

    public static <T> T newInstance(List<Field> fields, Class<T> clazz, Object ... params) throws BootException {
        if (ArrayUtil.isEmpty((Object[])params)) {
            Constructor constructor = ReflectUtil.getConstructor(clazz, (Class[])new Class[0]);
            try {
                return constructor.newInstance(new Object[0]);
            }
            catch (Exception e) {
                throw new BootException("Instance class [{}] error!");
            }
        }
        Class[] paramTypes = ReflectionUtils.getClasses(fields);
        Constructor constructor = ReflectUtil.getConstructor(clazz, (Class[])paramTypes);
        if (null == constructor) {
            throw new BootException("No Constructor matched for parameter types: [{}]");
        }
        try {
            return constructor.newInstance(params);
        }
        catch (Exception e) {
            throw new BootException("Instance class [{}] error!");
        }
    }

    public static Class<?>[] getClasses(List<Field> fields) {
        Class[] classes = new Class[fields.size()];
        for (int i = 0; i < fields.size(); ++i) {
            Field field = fields.get(i);
            classes[i] = field.getType();
        }
        return classes;
    }

    public static Boolean checkField(Class<?> dtoClass, Class<?> entityClass) {
        Field field;
        String filedName;
        Field[] declaredFields = dtoClass.getDeclaredFields();
        RecordComponent[] recordComponents = dtoClass.getRecordComponents();
        if (declaredFields.length == 0 && (Objects.isNull(recordComponents) || recordComponents.length == 0)) {
            throw new BusinessException("\u5b57\u6bb5\u53c2\u6570\u4e0d\u5b58\u5728");
        }
        if (declaredFields.length != 0) {
            for (AnnotatedElement annotatedElement : declaredFields) {
                filedName = ((Field)annotatedElement).getName();
                if ("serialVersionUID".equals(filedName) || !Objects.isNull(field = ReflectionUtils.findField(entityClass, filedName))) continue;
                throw new BusinessException(filedName + "\u5b57\u6bb5\u503c\u9519\u8bef");
            }
        }
        if (Objects.nonNull(recordComponents) && recordComponents.length != 0) {
            for (AnnotatedElement annotatedElement : recordComponents) {
                filedName = ((RecordComponent)annotatedElement).getName();
                if ("serialVersionUID".equals(filedName) || !Objects.isNull(field = ReflectionUtils.findField(entityClass, filedName))) continue;
                throw new BusinessException(filedName + "\u5b57\u6bb5\u503c\u9519\u8bef");
            }
        }
        return true;
    }

    public static Boolean checkField(String filedName, Class<?> entityClass) {
        Field field;
        if (!"serialVersionUID".equals(filedName) && Objects.isNull(field = ReflectionUtils.findField(entityClass, filedName))) {
            throw new BusinessException(filedName + "\u5b57\u6bb5\u503c\u9519\u8bef");
        }
        return true;
    }

    public static <T> T tryGetValue(Object obj, String path, T deft) {
        if (obj == null || path == null || path.length() == 0) {
            return deft;
        }
        Object object = obj;
        for (String name : path.split("\\.")) {
            if (object == null) break;
            Object value = ReflectionUtils.tryGetFieldValue(object, name, null);
            object = value == null ? ReflectionUtils.tryCallMethod(object, name, null, null) : value;
        }
        return (T)(object == null ? deft : object);
    }

    public static <T> T tryGetValue(Object obj, String path) {
        return ReflectionUtils.tryGetValue(obj, path, null);
    }
}

