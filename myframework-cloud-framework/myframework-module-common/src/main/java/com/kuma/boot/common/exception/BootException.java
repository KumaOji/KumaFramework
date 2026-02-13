/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.model.Code;

public class BootException
extends BaseException {
    public BootException() {
    }

    public BootException(String message) {
        super(message);
    }

    public BootException(Throwable e) {
        super(e);
    }

    public BootException(String message, Throwable e) {
        super(message, e);
    }

    public BootException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BootException(ResultEnum result) {
        super(result);
    }

    public BootException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public BootException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public BootException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public BootException(Code code, String message) {
        super(code, message);
    }

    public BootException(StatusEnum status, Code code, String message) {
        super(status, code, message);
    }

    public BootException(Code code, Throwable e) {
        super(code, e);
    }

    public BootException(StatusEnum status, Code code, Throwable e) {
        super(status, code, e);
    }

    public BootException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public BootException(StatusEnum status, Code code, Throwable e, String message) {
        super(status, code, e, message);
    }
}

