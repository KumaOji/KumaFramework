/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums;

import com.kuma.boot.common.enums.base.CommonEnum;

public enum LogOperateTypeEnum implements CommonEnum
{
    OPERATE_RECORD(1, "\u64cd\u4f5c\u8bb0\u5f55"),
    EXCEPTION_RECORD(2, "\u5f02\u5e38\u8bb0\u5f55");

    private final Integer code;
    private final String desc;

    private LogOperateTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getNameByCode(int code) {
        for (LogOperateTypeEnum result : LogOperateTypeEnum.values()) {
            if (result.getCode() != code) continue;
            return result.name().toLowerCase();
        }
        return null;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public int getCode() {
        return this.code;
    }
}

