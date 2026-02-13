/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums;

import java.util.HashMap;

public enum EventEnum {
    PropertyCacheUpdateEvent(HashMap.class, "\u5c5e\u6027\u7f13\u5b58\u66f4\u65b0\u4e8b\u4ef6");

    final Class<?> dataClass;
    final String desc;

    public Class<?> getDataClass() {
        return this.dataClass;
    }

    private EventEnum(Class<?> dataClass, String desc) {
        this.desc = desc;
        this.dataClass = dataClass;
    }

    public String getDesc() {
        return this.desc;
    }
}

