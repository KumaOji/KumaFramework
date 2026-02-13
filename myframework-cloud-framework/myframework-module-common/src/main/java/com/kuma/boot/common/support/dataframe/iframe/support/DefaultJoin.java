/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.support;

import com.kuma.boot.common.support.dataframe.iframe.support.Join;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public class DefaultJoin<T, K, R>
implements Join<T, K, R> {
    @Override
    public R join(T t, K k) {
        try {
            ParameterizedType parameterizedType = (ParameterizedType)this.getClass().getGenericSuperclass();
            Class rClass = (Class)parameterizedType.getActualTypeArguments()[2];
            Object r = rClass.newInstance();
            for (Field field : r.getClass().getDeclaredFields()) {
                Field kFeild;
                Field tFeild;
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldValue = null;
                if (t != null && (tFeild = t.getClass().getDeclaredField(fieldName)) != null) {
                    tFeild.setAccessible(true);
                    fieldValue = tFeild.get(t);
                }
                if (k != null && fieldValue == null && (kFeild = k.getClass().getDeclaredField(fieldName)) != null) {
                    kFeild.setAccessible(true);
                    fieldValue = kFeild.get(t);
                }
                field.set(r, fieldValue);
            }
            return (R)r;
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}

