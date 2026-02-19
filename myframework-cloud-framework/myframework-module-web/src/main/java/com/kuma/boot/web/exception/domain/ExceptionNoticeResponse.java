/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.exception.domain;

public class ExceptionNoticeResponse {
    private boolean success;
    private String errMsg;

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrMsg() {
        return this.errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}

