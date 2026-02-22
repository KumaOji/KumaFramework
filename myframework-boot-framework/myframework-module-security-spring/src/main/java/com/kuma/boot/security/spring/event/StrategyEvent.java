/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.common.JsonUtils
 */
package com.kuma.boot.security.spring.event;

import com.kuma.boot.common.utils.common.JsonUtils;

public interface StrategyEvent<T> {
    public void postLocalProcess(T var1);

    public void postRemoteProcess(String var1, String var2, String var3);

    public boolean isLocal(String var1);

    default public void postProcess(String originService, String destinationService, T data) {
        if (this.isLocal(destinationService)) {
            this.postLocalProcess(data);
        } else {
            this.postRemoteProcess(JsonUtils.toJson(data), originService, destinationService);
        }
    }
}

