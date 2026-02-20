/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.support.encryption.api.dto.req;

import com.kuma.boot.web.support.encryption.api.core.EncryptMask;

public class CommonEncryptRequest
extends CommonRequest {
    private String text;
    private EncryptMask encryptMask;

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public EncryptMask getEncryptMask() {
        return this.encryptMask;
    }

    public void setEncryptMask(EncryptMask encryptMask) {
        this.encryptMask = encryptMask;
    }
}

