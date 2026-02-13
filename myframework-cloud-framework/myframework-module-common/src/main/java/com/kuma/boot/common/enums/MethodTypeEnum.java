/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums;

import com.kuma.boot.common.enums.base.CommonEnum;

public enum MethodTypeEnum implements CommonEnum
{
    GET(1, "GET"),
    PUT(2, "PUT"),
    POST(3, "POST"),
    DELETE(4, "DELETE"),
    HEAD(5, "HEAD"),
    OPTIONS(6, "OPTIONS");

    private final int code;
    private final String desc;

    private MethodTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    public String getNameByCode(int code) {
        for (MethodTypeEnum result : MethodTypeEnum.values()) {
            if (result.getCode() != code) continue;
            return result.name().toLowerCase();
        }
        return null;
    }
}

