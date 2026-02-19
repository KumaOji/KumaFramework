/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.office.easyexcel.easyexcelcheck;

public class ExcelCheckErrDto<T> {
    private T t;
    private String errMsg;

    public ExcelCheckErrDto() {
    }

    public ExcelCheckErrDto(T t, String errMsg) {
        this.t = t;
        this.errMsg = errMsg;
    }

    public T getT() {
        return this.t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public String getErrMsg() {
        return this.errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}

