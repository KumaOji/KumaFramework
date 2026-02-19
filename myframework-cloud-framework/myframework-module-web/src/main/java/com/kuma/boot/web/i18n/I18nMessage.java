/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  io.swagger.v3.oas.annotations.media.Schema
 *  jakarta.validation.constraints.NotEmpty
 */
package com.kuma.boot.web.i18n;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(title="\u56fd\u9645\u5316\u4fe1\u606f")
public class I18nMessage {
    @NotEmpty(message="{i18nMessage.code}\uff1a{}")
    @Schema(title="\u56fd\u9645\u5316\u6807\u8bc6")
    private @NotEmpty(message="{i18nMessage.code}\uff1a{}") String code;
    @NotEmpty(message="{i18nMessage.message}\uff1a{}")
    @Schema(title="\u6587\u672c\u503c\uff0c\u53ef\u4ee5\u4f7f\u7528 { } \u52a0\u89d2\u6807\uff0c\u4f5c\u4e3a\u5360\u4f4d\u7b26")
    private @NotEmpty(message="{i18nMessage.message}\uff1a{}") String message;
    @NotEmpty(message="{i18nMessage.languageTag}\uff1a{}")
    @Schema(title="\u8bed\u8a00\u6807\u7b7e")
    private @NotEmpty(message="{i18nMessage.languageTag}\uff1a{}") String languageTag;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLanguageTag() {
        return this.languageTag;
    }

    public void setLanguageTag(String languageTag) {
        this.languageTag = languageTag;
    }
}

