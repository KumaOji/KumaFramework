/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.enums;

import com.kuma.boot.dingtalk.entity.MultiDingerConfig;

import java.util.HashMap;
import java.util.Map;

public enum MultiDingerConfigContainer {
    INSTANCE;

    public static final String GLOABL_KEY;
    private Map<String, MultiDingerConfig> container = new HashMap<String, MultiDingerConfig>();

    public MultiDingerConfig put(String key, MultiDingerConfig multiDingerConfig) {
        return this.container.put(key, multiDingerConfig);
    }

    public boolean isEmpty() {
        return this.container.isEmpty();
    }

    public MultiDingerConfig get(DingerType dingerType, String key) {
        key = String.valueOf((Object)dingerType) + "." + (String)key;
        if (this.container.containsKey(key)) {
            return this.container.get(key);
        }
        return this.container.get(String.valueOf((Object)dingerType) + "." + GLOABL_KEY);
    }

    public static void clear() {
        MultiDingerConfigContainer.INSTANCE.container.clear();
    }

    static {
        GLOABL_KEY = MultiDingerConfigContainer.class.getName();
    }
}

