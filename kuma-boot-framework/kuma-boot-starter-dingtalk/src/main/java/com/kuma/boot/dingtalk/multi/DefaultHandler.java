/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.multi;

import com.kuma.boot.dingtalk.model.DingerConfig;

import java.util.List;

public class DefaultHandler
implements AlgorithmHandler {
    @Override
    public DingerConfig handler(List<DingerConfig> dingerConfigs, DingerConfig defaultDingerConfig) {
        return defaultDingerConfig;
    }
}

