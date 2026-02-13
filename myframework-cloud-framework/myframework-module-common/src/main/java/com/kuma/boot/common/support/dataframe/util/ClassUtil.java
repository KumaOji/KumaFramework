/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassUtil {
    private ClassUtil() {
    }

    public static List<Field> findAllFiled(Class<?> clazz) {
        ArrayList<Field> resultList = new ArrayList<Field>();
        Class<?> targetClass = clazz;
        do {
            Field[] fields = targetClass.getDeclaredFields();
            resultList.addAll(Arrays.asList(fields));
        } while ((targetClass = targetClass.getSuperclass()) != null && targetClass != Object.class);
        return resultList;
    }
}

