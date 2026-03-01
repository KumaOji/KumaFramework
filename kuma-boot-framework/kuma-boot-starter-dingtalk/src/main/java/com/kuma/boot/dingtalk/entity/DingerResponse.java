/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

import com.kuma.boot.dingtalk.enums.DingerResponseCodeEnum;

public class DingerResponse {
    private String code;
    private String message;
    private String logid;
    private String data;

    private DingerResponse(DingerResponseCodeEnum resultCode) {
        this.code = resultCode.code();
        this.message = resultCode.message();
    }

    private DingerResponse(String logid, DingerResponseCodeEnum resultCode) {
        this(resultCode);
        this.logid = logid;
    }

    private DingerResponse(DingerResponseCodeEnum resultCode, String data) {
        this(resultCode);
        this.data = data;
    }

    private DingerResponse(DingerResponseCodeEnum resultCode, String logid, String data) {
        this(logid, resultCode);
        this.data = data;
    }

    public static <T> DingerResponse success(String logId, String data) {
        return new DingerResponse(DingerResponseCodeEnum.SUCCESS, logId, data);
    }

    public static <T> DingerResponse success(DingerResponseCodeEnum resultCode, String logId, String data) {
        return new DingerResponse(resultCode, logId, data);
    }

    public static DingerResponse failed(String logid) {
        return new DingerResponse(logid, DingerResponseCodeEnum.FAILED);
    }

    public static DingerResponse failed(String logid, DingerResponseCodeEnum resultCode) {
        return new DingerResponse(logid, resultCode);
    }

    public static DingerResponse failed(DingerResponseCodeEnum resultCode, String data) {
        return new DingerResponse(resultCode, data);
    }

    public static DingerResponse failed(DingerResponseCodeEnum resultCode) {
        return new DingerResponse(resultCode);
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLogid() {
        return this.logid;
    }

    public void setLogid(String logid) {
        this.logid = logid;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String toString() {
        return String.format("[code=%s, message=%s, logid=%s, data=%s]", this.code, this.message, this.logid, this.data);
    }
}

