//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.core.definition.enums;

public enum CaptchaResource {
    JIGSAW_ORIGINAL("Jigsaw original image", "滑动拼图底图"),
    JIGSAW_TEMPLATE("Jigsaw template image", "滑动拼图滑块底图"),
    WORD_CLICK("Word click image", "文字点选底图");

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
