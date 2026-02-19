/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnore
 */
package com.kuma.boot.web.gracefulresponse.defaults;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kuma.boot.web.gracefulresponse.data.Response;
import com.kuma.boot.web.gracefulresponse.data.ResponseStatus;
import java.util.Collections;

public class DefaultResponseImplStyle1
implements Response {
    private String code;
    private String msg;
    private Object data = Collections.emptyMap();

    @Override
    public void setStatus(ResponseStatus statusLine) {
        this.code = statusLine.getCode();
        this.msg = statusLine.getMsg();
    }

    @Override
    @JsonIgnore
    public ResponseStatus getStatus() {
        return null;
    }

    @Override
    public void setPayload(Object payload) {
        this.data = payload;
    }

    @Override
    @JsonIgnore
    public Object getPayload() {
        return null;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

