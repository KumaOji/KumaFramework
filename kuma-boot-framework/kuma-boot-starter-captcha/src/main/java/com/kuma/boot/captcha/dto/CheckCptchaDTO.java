/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.dto;

public class CheckCptchaDTO {
    private String captchaType;
    private String pointJson;
    private String token;

    public String getCaptchaType() {
        return this.captchaType;
    }

    public void setCaptchaType(String captchaType) {
        this.captchaType = captchaType;
    }

    public String getPointJson() {
        return this.pointJson;
    }

    public void setPointJson(String pointJson) {
        this.pointJson = pointJson;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

