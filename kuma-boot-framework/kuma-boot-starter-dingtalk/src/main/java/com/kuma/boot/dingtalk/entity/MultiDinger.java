/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

import com.kuma.boot.dingtalk.multi.DingerConfigHandler;

public class MultiDinger {
    private String key;
    private Class<? extends DingerConfigHandler> dingerConfigHandler;

    public MultiDinger(String key, Class<? extends DingerConfigHandler> dingerConfigHandler) {
        this.key = key;
        this.dingerConfigHandler = dingerConfigHandler;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Class<? extends DingerConfigHandler> getDingerConfigHandler() {
        return this.dingerConfigHandler;
    }

    public void setDingerConfigHandler(Class<? extends DingerConfigHandler> dingerConfigHandler) {
        this.dingerConfigHandler = dingerConfigHandler;
    }
}

