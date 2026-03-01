/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

import com.kuma.boot.dingtalk.model.DingerConfig;
import com.kuma.boot.dingtalk.multi.AlgorithmHandler;
import java.util.List;

public class MultiDingerAlgorithmDefinition {
    private String key;
    private Class<? extends AlgorithmHandler> algorithm;
    private List<DingerConfig> dingerConfigs;
    private String dingerConfigHandlerClassName;

    public MultiDingerAlgorithmDefinition(String key, Class<? extends AlgorithmHandler> algorithm, List<DingerConfig> dingerConfigs, String dingerConfigHandlerClassName) {
        this.key = key;
        this.algorithm = algorithm;
        this.dingerConfigs = dingerConfigs;
        this.dingerConfigHandlerClassName = dingerConfigHandlerClassName;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Class<? extends AlgorithmHandler> getAlgorithm() {
        return this.algorithm;
    }

    public void setAlgorithm(Class<? extends AlgorithmHandler> algorithm) {
        this.algorithm = algorithm;
    }

    public List<DingerConfig> getDingerConfigs() {
        return this.dingerConfigs;
    }

    public void setDingerConfigs(List<DingerConfig> dingerConfigs) {
        this.dingerConfigs = dingerConfigs;
    }

    public String getDingerConfigHandlerClassName() {
        return this.dingerConfigHandlerClassName;
    }

    public void setDingerConfigHandlerClassName(String dingerConfigHandlerClassName) {
        this.dingerConfigHandlerClassName = dingerConfigHandlerClassName;
    }
}

