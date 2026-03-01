/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.model;

public class DingerDefinitionGeneratorContext<T> {
    private String keyName;
    private T source;

    public DingerDefinitionGeneratorContext(String keyName, T source) {
        this.keyName = keyName;
        this.source = source;
    }

    public String getKeyName() {
        return this.keyName;
    }

    public T getSource() {
        return this.source;
    }
}

