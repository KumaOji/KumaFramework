/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.bean.BeanUtil
 *  cn.hutool.core.bean.copier.CopyOptions
 *  cn.hutool.core.lang.Assert
 *  com.fasterxml.jackson.annotation.JsonIgnore
 *  jakarta.annotation.Nullable
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.BeanWrapper
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.PropertyAccessorFactory
 *  org.springframework.cglib.beans.BeanCopier
 *  org.springframework.cglib.beans.BeanGenerator
 *  org.springframework.cglib.beans.BeanMap
 *  org.springframework.cglib.core.Converter
 *  org.springframework.util.FastByteArrayOutputStream
 *  tools.jackson.databind.BeanProperty
 */
package com.kuma.boot.common.utils.bean;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.Assert;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.utils.collection.MapUtils;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.common.OrikaUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.reflect.ClassUtils;
import jakarta.annotation.Nullable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.cglib.core.Converter;
import org.springframework.util.FastByteArrayOutputStream;
import tools.jackson.databind.BeanProperty;

public class BeanUtils
extends org.springframework.beans.BeanUtils {
    private BeanUtils() {
    }

    public static <T> T newInstance(Class<?> clazz) {
        return (T)BeanUtils.instantiateClass(clazz);
    }

    public static <T> T newInstance(String clazzStr) {
        try {
            Class clazz = ClassUtils.forName((String)clazzStr, null);
            return BeanUtils.newInstance(clazz);
        }
        catch (ClassNotFoundException e) {
            throw new BaseException(e);
        }
    }

    @Nullable
    public static Object getProperty(@Nullable Object bean, String propertyName) {
        if (bean == null) {
            return null;
        }
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess((Object)bean);
        return beanWrapper.getPropertyValue(propertyName);
    }

    public static void setProperty(Object bean, String propertyName, Object value) {
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess((Object)Objects.requireNonNull(bean, "bean Could not null"));
        beanWrapper.setPropertyValue(propertyName, value);
    }

    @Nullable
    public static <T> T clone(@Nullable T source) {
        if (source == null) {
            return null;
        }
        return (T)BeanUtils.copy(source, source.getClass());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public static <T> T deepClone(@Nullable T source) {
        if (source == null) {
            return null;
        }
        FastByteArrayOutputStream fBos = new FastByteArrayOutputStream(1024);
        try (ObjectOutputStream oos = new ObjectOutputStream((OutputStream)fBos);){
            oos.writeObject(source);
            oos.flush();
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Failed to serialize object of type: " + String.valueOf(source.getClass()), ex);
        }
        try (ObjectInputStream ois = new ObjectInputStream(fBos.getInputStream());){
            Object object = ois.readObject();
            return (T)object;
        }
        catch (IOException | ClassNotFoundException ex) {
            throw new IllegalArgumentException("Failed to deserialize object", ex);
        }
    }

    @Nullable
    public static <T> T copy(@Nullable Object source, Class<T> clazz) {
        if (source == null) {
            return null;
        }
        return BeanUtils.copy(source, source.getClass(), clazz);
    }

    @Nullable
    public static <T> T copy(@Nullable Object source, Class sourceClazz, Class<T> targetClazz) {
        if (source == null) {
            return null;
        }
        BeanCopier copier = BeanCopier.create((Class)sourceClazz, targetClazz, (boolean)false);
        T to = BeanUtils.newInstance(targetClazz);
        copier.copy(source, to, null);
        return to;
    }

    public static <T> List<T> copy(@Nullable Collection<?> sourceList, Class<T> targetClazz) {
        return BeanUtils.copy(sourceList, (List)null, targetClazz);
    }

    public static <T> List<T> copy(@Nullable Collection<?> sourceList, @Nullable List<T> targetList, Class<T> targetClazz) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }
        if (targetList == null) {
            targetList = new ArrayList<T>(sourceList.size());
        }
        Class<?> sourceClazz = null;
        for (Object source : sourceList) {
            if (source == null) continue;
            if (sourceClazz == null) {
                sourceClazz = source.getClass();
            }
            T bean = BeanUtils.copy(source, sourceClazz, targetClazz);
            targetList.add(bean);
        }
        return targetList;
    }

    public static void copy(@Nullable Object source, @Nullable Object targetBean) {
        if (source == null || targetBean == null) {
            return;
        }
        BeanCopier copier = BeanCopier.create(source.getClass(), targetBean.getClass(), (boolean)false);
        copier.copy(source, targetBean, null);
    }

    public static void copyNonNull(@Nullable Object source, @Nullable Object targetBean) {
        if (source == null || targetBean == null) {
            return;
        }
        BeanCopier copier = BeanCopier.create(source.getClass(), targetBean.getClass(), (boolean)false);
        copier.copy(source, targetBean, null);
    }

    @Nullable
    public static <T> T copyWithConvert(@Nullable Object source, Class<T> targetClazz) {
        if (source == null) {
            return null;
        }
        return BeanUtils.copyWithConvert(source, source.getClass(), targetClazz);
    }

    @Nullable
    public static <T> T copyWithConvert(@Nullable Object source, Class<?> sourceClazz, Class<T> targetClazz) {
        if (source == null) {
            return null;
        }
        BeanCopier copier = BeanCopier.create(sourceClazz, targetClazz, (boolean)true);
        T to = BeanUtils.newInstance(targetClazz);
        copier.copy(source, to, (Converter)new com.kuma.boot.common.utils.convert.Converter(sourceClazz, targetClazz));
        return to;
    }

    public static <T> List<T> copyWithConvert(@Nullable Collection<?> sourceList, Class<T> targetClazz) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<T> outList = new ArrayList<T>(sourceList.size());
        Class<?> sourceClazz = null;
        for (Object source : sourceList) {
            if (source == null) continue;
            if (sourceClazz == null) {
                sourceClazz = source.getClass();
            }
            T bean = BeanUtils.copyWithConvert(source, sourceClazz, targetClazz);
            outList.add(bean);
        }
        return outList;
    }

    @Nullable
    public static <T> T copyProperties(@Nullable Object source, Class<T> targetClazz) throws BeansException {
        if (source == null) {
            return null;
        }
        T to = BeanUtils.newInstance(targetClazz);
        BeanUtils.copyProperties(source, to);
        return to;
    }

    public static <T> List<T> copyProperties(@Nullable Collection<?> sourceList, Class<T> targetClazz) throws BeansException {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<T> outList = new ArrayList<T>(sourceList.size());
        for (Object source : sourceList) {
            if (source == null) continue;
            T bean = BeanUtils.copyProperties(source, targetClazz);
            outList.add(bean);
        }
        return outList;
    }

    public static Map<String, Object> toMap(@Nullable Object bean) {
        if (bean == null) {
            return new HashMap<String, Object>(0);
        }
        return BeanMap.create((Object)bean);
    }

    public static Map<String, Object> toNewMap(@Nullable Object bean) {
        return new HashMap<String, Object>(BeanUtils.toMap(bean));
    }

    public static <T> T toBean(Map<String, Object> beanMap, Class<T> valueType) {
        Objects.requireNonNull(beanMap, "beanMap Could not null");
        T to = BeanUtils.newInstance(valueType);
        if (beanMap.isEmpty()) {
            return to;
        }
        BeanUtils.copy(beanMap, to);
        return to;
    }

    @Nullable
    public static Object generator(@Nullable Object superBean, BeanProperty ... props) {
        if (superBean == null) {
            return null;
        }
        Class<?> superclass = superBean.getClass();
        Object genBean = BeanUtils.generator(superclass, props);
        BeanUtils.copy(superBean, genBean);
        return genBean;
    }

    public static Object generator(Class<?> superclass, BeanProperty ... props) {
        BeanGenerator generator = new BeanGenerator();
        generator.setSuperclass(superclass);
        generator.setUseCache(true);
        generator.setContextClass(superclass);
        for (BeanProperty prop : props) {
            generator.addProperty(prop.getName(), prop.getClass());
        }
        return generator.create();
    }

    public static BeanDiff diff(Object src, Object dist) {
        Assert.notNull((Object)src, (String)"diff Object src is null.", (Object[])new Object[0]);
        Assert.notNull((Object)src, (String)"diff Object dist is null.", (Object[])new Object[0]);
        return BeanUtils.diff(BeanUtils.toMap(src), BeanUtils.toMap(dist));
    }

    public static BeanDiff diff(Map<String, Object> src, Map<String, Object> dist) {
        Assert.notNull(src, (String)"diff Map src is null.", (Object[])new Object[0]);
        Assert.notNull(src, (String)"diff Map dist is null.", (Object[])new Object[0]);
        HashMap<String, Object> difference = new HashMap<String, Object>(8);
        difference.putAll(src);
        difference.putAll(dist);
        difference.entrySet().removeAll(src.entrySet());
        HashMap oldValues = new HashMap(8);
        difference.keySet().forEach(k -> oldValues.put(k, src.get(k)));
        BeanDiff diff = new BeanDiff();
        diff.getFields().addAll(difference.keySet());
        diff.getOldValues().putAll(oldValues);
        diff.getNewValues().putAll(difference);
        return diff;
    }

    public static void copyIgnoredNull(Object source, Object target) {
        BeanUtil.copyProperties((Object)source, (Object)target, (CopyOptions)CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    }

    public static void copyIncludeNull(Object source, Object target) {
        BeanUtil.copyProperties((Object)source, (Object)target, (CopyOptions)CopyOptions.create().setIgnoreNullValue(true));
    }

    public static <T> T tryConvert(Object value, Class<T> type) {
        try {
            return OrikaUtils.convert(value, type);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Map<String, Object> beanToMap(Object bean) {
        ArgUtils.notNull(bean, "bean");
        try {
            Field[] fieldList;
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
            for (Field field : fieldList = bean.getClass().getFields()) {
                String fieldName = field.getName();
                Object fieldValue = field.get(bean);
                map.put(fieldName, fieldValue);
            }
            return map;
        }
        catch (IllegalAccessException e) {
            throw new BootException(e);
        }
    }

    public static void mapToBean(Map<String, Object> map, Object bean) {
        ArgUtils.notNull(bean, "bean");
        if (MapUtils.isEmpty(map)) {
            return;
        }
        try {
            Field[] fieldList;
            for (Field field : fieldList = bean.getClass().getFields()) {
                String fieldName = field.getName();
                Object fieldValue = map.get(fieldName);
                if (!ObjectUtils.isNotNull(fieldValue)) continue;
                field.set(bean, fieldValue);
            }
        }
        catch (IllegalAccessException e) {
            throw new BootException(e);
        }
    }

    public static void copyProperties(Object source, Object target) {
        ObjectUtils.copyProperties(source, target);
    }

    public static class BeanDiff {
        @JsonIgnore
        private transient Set<String> fields = new HashSet<String>();
        @JsonIgnore
        private transient Map<String, Object> oldValues = new HashMap<String, Object>();
        @JsonIgnore
        private transient Map<String, Object> newValues = new HashMap<String, Object>();

        public Set<String> getFields() {
            return this.fields;
        }

        public Map<String, Object> getOldValues() {
            return this.oldValues;
        }

        public Map<String, Object> getNewValues() {
            return this.newValues;
        }

        public void setFields(Set<String> fields) {
            this.fields = fields;
        }

        public void setOldValues(Map<String, Object> oldValues) {
            this.oldValues = oldValues;
        }

        public void setNewValues(Map<String, Object> newValues) {
            this.newValues = newValues;
        }
    }
}

