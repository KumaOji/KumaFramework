/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.multi;

import com.kuma.boot.dingtalk.model.DingerConfig;
import java.util.List;

public interface AlgorithmHandler {
    public static final int DEFAULT_INDEX = 0;
    public static final String MULTI_DINGER_PRIORITY_EXECUTE = "multiDingerAlgorithmInjectRegister";

    public DingerConfig handler(List<DingerConfig> var1, DingerConfig var2);

    default public DingerConfig dingerConfig(List<DingerConfig> dingerConfigs, DingerConfig defaultDingerConfig) {
        if (dingerConfigs == null || dingerConfigs.isEmpty()) {
            return defaultDingerConfig;
        }
        if (dingerConfigs.size() == 1) {
            return dingerConfigs.get(0);
        }
        return this.handler(dingerConfigs, defaultDingerConfig);
    }

    default public String algorithmId() {
        return this.getClass().getSimpleName();
    }
}

