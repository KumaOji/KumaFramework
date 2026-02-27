/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.model;

public abstract class DingerDefinitionGenerator<T> {
    protected DingerDefinitionGenerator() {
        this.register(this);
    }

    public abstract DingerDefinition generator(DingerDefinitionGeneratorContext<T> var1);

    protected String key() {
        return this.getClass().getName();
    }

    private void register(DingerDefinitionGenerator dingerDefinitionGenerator) {
        DingerDefinitionGeneratorFactory.dingTalkDefinitionGeneratorMap.put(this.key(), dingerDefinitionGenerator);
    }
}

