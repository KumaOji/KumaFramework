/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.cloud.stream.framework.rocketmq.tags;

public enum OrderTagsEnum {
    ORDER_CREATE("\u8ba2\u5355\u521b\u5efa"),
    STATUS_CHANGE("\u8ba2\u5355\u72b6\u6001\u6539\u53d8");

    private final String description;

    private OrderTagsEnum(String description) {
        this.description = description;
    }

    public String description() {
        return this.description;
    }
}

