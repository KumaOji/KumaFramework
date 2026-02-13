/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums;

import com.kuma.boot.common.enums.base.CommonEnum;

public enum AliPayStatusEnum implements CommonEnum
{
    FINISHED(1, "TRADE_FINISHED", "\u4ea4\u6613\u6210\u529f"),
    SUCCESS(2, "TRADE_SUCCESS", "\u652f\u4ed8\u6210\u529f"),
    BUYER_PAY(3, "WAIT_BUYER_PAY", "\u4ea4\u6613\u521b\u5efa"),
    CLOSED(4, "TRADE_CLOSED", "\u4ea4\u6613\u5173\u95ed");

    private final int code;
    private final String value;
    private final String desc;

    private AliPayStatusEnum(int code, String value, String desc) {
        this.code = code;
        this.desc = desc;
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }
}

