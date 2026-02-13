/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums.base;

public interface SelfDescribedEnum {
    default public String getName() {
        return this.name();
    }

    public String name();

    public String getDesc();
}

