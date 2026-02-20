/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.support.encryption.api.dto.req;

public class CommonDecryptRequest
extends CommonRequest {
    private String cipher;

    public String getCipher() {
        return this.cipher;
    }

    public void setCipher(String cipher) {
        this.cipher = cipher;
    }
}

