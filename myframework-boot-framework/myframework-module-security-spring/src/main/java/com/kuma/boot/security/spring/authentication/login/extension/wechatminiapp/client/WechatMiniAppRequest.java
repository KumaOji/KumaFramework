/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.client;

public class WechatMiniAppRequest {
    private String clientId;
    private String openId;
    private String unionId;
    private String iv;
    private String encryptedData;
    private String signature;
    private String rawData;

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getOpenId() {
        return this.openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUnionId() {
        return this.unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getIv() {
        return this.iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getEncryptedData() {
        return this.encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getRawData() {
        return this.rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }
}

