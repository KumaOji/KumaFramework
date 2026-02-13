/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.reflect;

import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.reflect.ClassUtils;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public final class PropertyDescriptorUtils {
    private PropertyDescriptorUtils() {
    }

    public static PropertyDescriptor getPropertyDescriptor(Class beanClass, String propertyName) {
        ArgUtils.notNull(beanClass, "beanClass");
        ArgUtils.notEmpty(propertyName, "propertyName");
        try {
            return new PropertyDescriptor(propertyName, beanClass);
        }
        catch (IntrospectionException e) {
            throw new BootException(e);
        }
    }

    public static Method getReadMethod(Class<?> beanClass, String propertyName) {
        PropertyDescriptor propertyDescriptor = PropertyDescriptorUtils.getPropertyDescriptor(beanClass, propertyName);
        return propertyDescriptor.getReadMethod();
    }

    public static Method getWriteMethod(Class beanClass, String propertyName) {
        PropertyDescriptor propertyDescriptor = PropertyDescriptorUtils.getPropertyDescriptor(beanClass, propertyName);
        return propertyDescriptor.getWriteMethod();
    }

    public static Class<?> getPropertyType(Class beanClass, String propertyName) {
        PropertyDescriptor propertyDescriptor = PropertyDescriptorUtils.getPropertyDescriptor(beanClass, propertyName);
        return propertyDescriptor.getPropertyType();
    }

    public static List<PropertyDescriptor> getAllPropertyDescriptorList(Class beanClass) {
        ArgUtils.notNull(beanClass, "beanClass");
        List<Field> fieldList = ClassUtils.getAllFieldList(beanClass);
        return CollectionUtils.toList(fieldList, field -> {
            String fieldName = field.getName();
            return PropertyDescriptorUtils.getPropertyDescriptor(beanClass, fieldName);
        });
    }

    public static boolean setPropertyValue(Object bean, PropertyDescriptor descriptor, Object value) {
        try {
            Method setMethod = descriptor.getWriteMethod();
            if (setMethod == null) {
                return false;
            }
            setMethod.invoke(bean, value);
            return true;
        }
        catch (Exception e) {
            throw new BootException(e);
        }
    }

    public static boolean setPropertyValue(Object bean, String descriptorName, Object value) {
        PropertyDescriptor propertyDescriptor = PropertyDescriptorUtils.getPropertyDescriptor(bean.getClass(), descriptorName);
        return PropertyDescriptorUtils.setPropertyValue(bean, propertyDescriptor, value);
    }

    public static Object getPropertyValue(Object bean, PropertyDescriptor descriptor) {
        try {
            Method readMethod = descriptor.getReadMethod();
            if (readMethod == null) {
                return null;
            }
            return readMethod.invoke(bean, new Object[0]);
        }
        catch (Exception e) {
            throw new BootException(e);
        }
    }

    public static Object getPropertyValue(Object bean, String descriptorName) {
        PropertyDescriptor propertyDescriptor = PropertyDescriptorUtils.getPropertyDescriptor(bean.getClass(), descriptorName);
        return PropertyDescriptorUtils.getPropertyValue(bean, propertyDescriptor);
    }
}

