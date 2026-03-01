/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.cloud.stream.framework.rocketmq.tags;

public enum MemberTagsEnum {
    MEMBER_REGISTER("\u4f1a\u5458\u6ce8\u518c"),
    MEMBER_LOGIN("\u4f1a\u5458\u767b\u5f55"),
    MEMBER_SING("\u4f1a\u5458\u7b7e\u5230"),
    MEMBER_WITHDRAWAL("\u4f1a\u5458\u63d0\u73b0"),
    MEMBER_POINT_CHANGE("\u4f1a\u5458\u79ef\u5206\u53d8\u52a8");

    private final String description;

    private MemberTagsEnum(String description) {
        this.description = description;
    }

    public String description() {
        return this.description;
    }
}

