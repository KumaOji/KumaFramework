/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.support.encryption.api.dto.resp;

public class CommonDecryptResponse
extends CommonResponse {
    private String text;

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

