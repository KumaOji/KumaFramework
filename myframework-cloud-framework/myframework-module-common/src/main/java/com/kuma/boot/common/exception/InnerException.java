/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class InnerException
extends BootException {
    private static final long serialVersionUID = 6610083281801529147L;

    public InnerException() {
    }

    public InnerException(String message) {
        super(message);
    }

    public InnerException(Throwable e) {
        super(e);
    }

    public InnerException(String message, Throwable e) {
        super(message, e);
    }

    public InnerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InnerException(ResultEnum result) {
        super(result);
    }

    public InnerException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public InnerException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public InnerException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public InnerException(Code code, String message) {
        super(code, message);
    }

    public InnerException(StatusEnum status, Code code, String message) {
        super(status, code, message);
    }

    public InnerException(Code code, Throwable e) {
        super(code, e);
    }

    public InnerException(StatusEnum status, Code code, Throwable e) {
        super(status, code, e);
    }

    public InnerException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public InnerException(StatusEnum status, Code code, Throwable e, String message) {
        super(status, code, e, message);
    }
}

