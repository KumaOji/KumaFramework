/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

import com.kuma.boot.dingtalk.exception.DingerException;

public class DingerCallback<T> {
    private String dkid;
    private T message;
    private DingerException ex;

    public DingerCallback() {
    }

    public DingerCallback(String dkid, T message, DingerException ex) {
        this.dkid = dkid;
        this.message = message;
        this.ex = ex;
    }

    public String getDkid() {
        return this.dkid;
    }

    public void setDkid(String dkid) {
        this.dkid = dkid;
    }

    public T getMessage() {
        return this.message;
    }

    public void setMessage(T message) {
        this.message = message;
    }

    public DingerException getEx() {
        return this.ex;
    }

    public void setEx(DingerException ex) {
        this.ex = ex;
    }
}

