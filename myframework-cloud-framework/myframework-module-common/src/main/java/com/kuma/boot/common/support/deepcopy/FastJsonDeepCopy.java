/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson2.JSON
 *  com.alibaba.fastjson2.JSONWriter$Feature
 */
package com.kuma.boot.common.support.deepcopy;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.kuma.boot.common.support.deepcopy.AbstractDeepCopy;

public class FastJsonDeepCopy
extends AbstractDeepCopy {
    private static final FastJsonDeepCopy INSTANCE = new FastJsonDeepCopy();

    public static FastJsonDeepCopy getInstance() {
        return INSTANCE;
    }

    @Override
    protected <T> T doDeepCopy(T object) {
        Class<?> clazz = object.getClass();
        String jsonString = JSON.toJSONString(object, (JSONWriter.Feature[])new JSONWriter.Feature[]{JSONWriter.Feature.ReferenceDetection});
        return (T)JSON.parseObject((String)jsonString, clazz);
    }
}

