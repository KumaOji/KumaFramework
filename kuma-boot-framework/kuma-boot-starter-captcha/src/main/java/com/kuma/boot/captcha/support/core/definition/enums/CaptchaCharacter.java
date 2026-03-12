//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.core.definition.enums;

import com.kuma.boot.captcha.support.core.provider.RandomProvider;

public enum CaptchaCharacter {
    NUM_AND_CHAR(0, RandomProvider.CHAR_MAX_INDEX, "数字和字母混合"),
    ONLY_NUM(0, 8, "纯数字"),
    ONLY_CHAR(8, RandomProvider.CHAR_MAX_INDEX, "纯字母"),
    ONLY_UPPER_CHAR(8, 31, "纯大写字母"),
    ONLY_LOWER_CHAR(31, RandomProvider.LOWER_MAX_INDEX, "纯小写字母"),
    NUM_AND_UPPER_CHAR(0, 31, "数字和大写字母");

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
