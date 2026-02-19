/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.gracefulresponse.defaults;

import com.kuma.boot.web.gracefulresponse.data.ResponseStatus;

public class DefaultResponseStatus
implements ResponseStatus {
    private String code;
    private String msg;

    public DefaultResponseStatus() {
    }

    public DefaultResponseStatus(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}

