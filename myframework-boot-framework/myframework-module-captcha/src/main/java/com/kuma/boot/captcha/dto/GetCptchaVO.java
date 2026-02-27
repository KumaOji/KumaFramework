/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.dto;

public class GetCptchaVO {
    private String originalImageBase64;
    private String jigsawImageBase64;
    private String token;
    private String secretKey;

    public String getOriginalImageBase64() {
        return this.originalImageBase64;
    }

    public void setOriginalImageBase64(String originalImageBase64) {
        this.originalImageBase64 = originalImageBase64;
    }

    public String getJigsawImageBase64() {
        return this.jigsawImageBase64;
    }

    public void setJigsawImageBase64(String jigsawImageBase64) {
        this.jigsawImageBase64 = jigsawImageBase64;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}

