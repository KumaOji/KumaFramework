/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.gracefulresponse;

public class GracefulResponseException
extends RuntimeException {
    private String code;
    private String msg;

    public GracefulResponseException() {
    }

    public GracefulResponseException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public GracefulResponseException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public GracefulResponseException(String msg, Throwable cause) {
        super(msg, cause);
        this.msg = msg;
    }

    public GracefulResponseException(String code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}

