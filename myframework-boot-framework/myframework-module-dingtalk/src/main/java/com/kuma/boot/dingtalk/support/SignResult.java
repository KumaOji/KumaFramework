/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.support;

public class SignResult
extends SignBase {
    private String sign;
    private Long timestamp;

    public SignResult() {
    }

    public SignResult(String sign, Long timestamp) {
        this.sign = sign;
        this.timestamp = timestamp;
    }

    public String getSign() {
        return this.sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String transfer() {
        StringBuilder signStr = new StringBuilder("&");
        signStr.append("sign=").append(this.sign).append("&").append("timestamp=").append(this.timestamp);
        return signStr.toString();
    }
}

