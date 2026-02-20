/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.support.encryption.api.core;

public class EncryptMaskContext {
    private String plainText;
    private String type;

    public String getPlainText() {
        return this.plainText;
    }

    public void setPlainText(String plainText) {
        this.plainText = plainText;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return "EncryptMaskContext{plainText='" + this.plainText + "', type='" + this.type + "'}";
    }
}

