/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.dto;

public class GetCptchaDTO {
    private String captchaType;
    private String clientUid;

    public String getCaptchaType() {
        return this.captchaType;
    }

    public void setCaptchaType(String captchaType) {
        this.captchaType = captchaType;
    }

    public String getClientUid() {
        return this.clientUid;
    }

    public void setClientUid(String clientUid) {
        this.clientUid = clientUid;
    }
}

