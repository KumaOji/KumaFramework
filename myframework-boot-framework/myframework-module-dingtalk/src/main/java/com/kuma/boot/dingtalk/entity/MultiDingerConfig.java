/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

import com.kuma.boot.dingtalk.model.DingerConfig;
import com.kuma.boot.dingtalk.multi.AlgorithmHandler;
import java.util.List;

public class MultiDingerConfig {
    private AlgorithmHandler algorithmHandler;
    private List<DingerConfig> dingerConfigs;

    public MultiDingerConfig(AlgorithmHandler algorithmHandler, List<DingerConfig> dingerConfigs) {
        this.algorithmHandler = algorithmHandler;
        this.dingerConfigs = dingerConfigs;
    }

    public AlgorithmHandler getAlgorithmHandler() {
        return this.algorithmHandler;
    }

    public void setAlgorithmHandler(AlgorithmHandler algorithmHandler) {
        this.algorithmHandler = algorithmHandler;
    }

    public List<DingerConfig> getDingerConfigs() {
        return this.dingerConfigs;
    }

    public void setDingerConfigs(List<DingerConfig> dingerConfigs) {
        this.dingerConfigs = dingerConfigs;
    }
}

