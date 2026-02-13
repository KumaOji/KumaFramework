/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson2.JSON
 */
package com.kuma.boot.common.support.dataframe.util;

import com.alibaba.fastjson2.JSON;

public class BeanCopyUtil {
    private BeanCopyUtil() {
    }

    public static <T> T copyProperties(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        Object t = JSON.parseObject((String)JSON.toJSONString((Object)source), targetClass);
        return (T)t;
    }
}

