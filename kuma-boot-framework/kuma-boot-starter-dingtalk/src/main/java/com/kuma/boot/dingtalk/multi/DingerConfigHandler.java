/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.multi;

import com.kuma.boot.dingtalk.model.DingerConfig;

import java.util.List;

public interface DingerConfigHandler {
    public List<DingerConfig> dingerConfigs();

    default public Class<? extends AlgorithmHandler> algorithmHandler() {
        return DingerHandler.class;
    }
}

