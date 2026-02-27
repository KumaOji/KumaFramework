/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.dto;

public class CheckCptchaVO {
    private String pointJson;
    private String token;
    private boolean result;

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

    public boolean isResult() {
        return this.result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}

