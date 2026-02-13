/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson2.JSON
 */
package com.kuma.boot.common.support.serializer.impl;

import com.alibaba.fastjson2.JSON;
import com.kuma.boot.common.support.serializer.impl.StringSerializer;

public class FastJsonSerializer
extends StringSerializer {
    @Override
    public byte[] serialize(Object data) {
        String jsonString = JSON.toJSONString((Object)data);
        return super.serialize(jsonString);
    }

    @Override
    public Object deserialize(byte[] bytes, ClassLoader classLoader) {
        String deserialize = String.valueOf(super.deserialize(bytes, classLoader));
        return JSON.parseObject((String)deserialize);
    }

    @Override
    public String name() {
        return "fastjson";
    }
}

