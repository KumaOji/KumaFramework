/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.cloud.stream.framework.rocketmq.tags;

public enum AfterSaleTagsEnum {
    REFUND("\u552e\u540e\u9000\u6b3e"),
    AFTER_SALE_STATUS_CHANGE("\u552e\u540e\u5355\u72b6\u6001\u6539\u53d8");

    private final String description;

    private AfterSaleTagsEnum(String description) {
        this.description = description;
    }

    public String description() {
        return this.description;
    }
}

