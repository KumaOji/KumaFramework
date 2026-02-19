/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.i18n;

public class I18nOptions {
    private String fallbackLanguageTag = "zh-CN";
    private boolean useCodeAsDefaultMessage = true;

    public String getFallbackLanguageTag() {
        return this.fallbackLanguageTag;
    }

    public void setFallbackLanguageTag(String fallbackLanguageTag) {
        this.fallbackLanguageTag = fallbackLanguageTag;
    }

    public boolean isUseCodeAsDefaultMessage() {
        return this.useCodeAsDefaultMessage;
    }

    public void setUseCodeAsDefaultMessage(boolean useCodeAsDefaultMessage) {
        this.useCodeAsDefaultMessage = useCodeAsDefaultMessage;
    }
}

