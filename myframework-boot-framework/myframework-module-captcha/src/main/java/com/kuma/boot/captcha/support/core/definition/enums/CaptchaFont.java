/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.support.core.definition.enums;

public enum CaptchaFont {
    ACTION("Action.ttf"),
    BEATAE("Beatae.ttf"),
    EPILOG("Epilog.ttf"),
    FRESNEL("Fresnel.ttf"),
    HEADACHE("Headache.ttf"),
    LEXOGRAPHER("Lexographer.ttf"),
    PREFIX("Prefix"),
    PROG_BOT("ProgBot"),
    ROBOT_TEACHER("RobotTeacher.ttf"),
    SCANDAL("Scandal.ttf");

    private final String fontName;

    private CaptchaFont(String fontName) {
        this.fontName = fontName;
    }

    public String getFontName() {
        return this.fontName;
    }
}

