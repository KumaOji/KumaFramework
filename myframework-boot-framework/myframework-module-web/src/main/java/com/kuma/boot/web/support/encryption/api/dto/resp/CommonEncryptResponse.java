/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.support.encryption.api.dto.resp;

public class CommonEncryptResponse
extends CommonResponse {
    private String cipher;
    private String mask;
    private String hash;

    public String getCipher() {
        return this.cipher;
    }

    public void setCipher(String cipher) {
        this.cipher = cipher;
    }

    public String getMask() {
        return this.mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "CommonEncryptResponse{cipher='" + this.cipher + "', mask='" + this.mask + "', hash='" + this.hash + "'}";
    }
}

