/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.generator.bank;

public enum BankCardTypeEnum {
    DEBIT("\u501f\u8bb0\u5361/\u50a8\u84c4\u5361"),
    CREDIT("\u4fe1\u7528\u5361/\u8d37\u8bb0\u5361");

    private final String name;

    private BankCardTypeEnum(String name) {
        this.name = name;
    }
}

