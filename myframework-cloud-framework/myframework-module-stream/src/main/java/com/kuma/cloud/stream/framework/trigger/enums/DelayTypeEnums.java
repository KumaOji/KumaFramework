/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.cloud.stream.framework.trigger.enums;

public enum DelayTypeEnums {
    PROMOTION("\u4fc3\u9500\u6d3b\u52a8"),
    PINTUAN_ORDER("\u62fc\u56e2\u8ba2\u5355"),
    BROADCAST("\u76f4\u64ad");

    private String description;

    private DelayTypeEnums(String description) {
        this.description = description;
    }
}

