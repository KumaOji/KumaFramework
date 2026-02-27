/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.support.core.definition.enums;

public enum CaptchaResource {
    JIGSAW_ORIGINAL("Jigsaw original image", "\u6ed1\u52a8\u62fc\u56fe\u5e95\u56fe"),
    JIGSAW_TEMPLATE("Jigsaw template image", "\u6ed1\u52a8\u62fc\u56fe\u6ed1\u5757\u5e95\u56fe"),
    WORD_CLICK("Word click image", "\u6587\u5b57\u70b9\u9009\u5e95\u56fe");

    private final String content;
    private final String description;

    private CaptchaResource(String type, String description) {
        this.content = type;
        this.description = description;
    }

    public String getContent() {
        return this.content;
    }

    public String getDescription() {
        return this.description;
    }
}

