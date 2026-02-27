/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.support.core.definition.enums;

import com.kuma.boot.captcha.support.core.provider.RandomProvider;

public enum CaptchaCharacter {
    NUM_AND_CHAR(0, RandomProvider.CHAR_MAX_INDEX, "\u6570\u5b57\u548c\u5b57\u6bcd\u6df7\u5408"),
    ONLY_NUM(0, 8, "\u7eaf\u6570\u5b57"),
    ONLY_CHAR(8, RandomProvider.CHAR_MAX_INDEX, "\u7eaf\u5b57\u6bcd"),
    ONLY_UPPER_CHAR(8, 31, "\u7eaf\u5927\u5199\u5b57\u6bcd"),
    ONLY_LOWER_CHAR(31, RandomProvider.LOWER_MAX_INDEX, "\u7eaf\u5c0f\u5199\u5b57\u6bcd"),
    NUM_AND_UPPER_CHAR(0, 31, "\u6570\u5b57\u548c\u5927\u5199\u5b57\u6bcd");

    private final int start;
    private final int end;
    private final String description;

    private CaptchaCharacter(int start, int end, String description) {
        this.start = start;
        this.end = end;
        this.description = description;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public String getDescription() {
        return this.description;
    }
}

