/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  io.swagger.v3.oas.annotations.media.Schema
 */
package com.kuma.boot.captcha.support.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public abstract class Captcha {
    @Schema(title="\u9a8c\u8bc1\u7801\u8eab\u4efd")
    private String identity;
    @Schema(title="\u9a8c\u8bc1\u7801\u7c7b\u522b")
    private String category;

    public String getIdentity() {
        return this.identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

