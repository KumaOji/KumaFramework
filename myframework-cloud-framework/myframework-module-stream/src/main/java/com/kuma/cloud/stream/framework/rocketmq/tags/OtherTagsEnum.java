/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.cloud.stream.framework.rocketmq.tags;

public enum OtherTagsEnum {
    MESSAGE("\u7ad9\u5185\u6d88\u606f\u63d0\u9192"),
    SMS("\u77ed\u4fe1\u6d88\u606f\u63d0\u9192");

    private final String description;

    private OtherTagsEnum(String description) {
        this.description = description;
    }

    public String description() {
        return this.description;
    }
}

