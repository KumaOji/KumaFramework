/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.model.result;

import java.io.Serializable;

public class ExceptionResult
implements Serializable {
    private static final long serialVersionUID = -3685249101751401211L;
    private String msg;

    public ExceptionResult(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

