/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class CheckException
extends BootException {
    public CheckException() {
    }

    public CheckException(String message) {
        super(message);
    }

    public CheckException(Throwable e) {
        super(e);
    }

    public CheckException(String message, Throwable e) {
        super(message, e);
    }

    public CheckException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CheckException(ResultEnum result) {
        super(result);
    }

    public CheckException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public CheckException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public CheckException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public CheckException(Code code, String message) {
        super(code, message);
    }

    public CheckException(StatusEnum status, Code code, String message) {
        super(status, code, message);
    }

    public CheckException(Code code, Throwable e) {
        super(code, e);
    }

    public CheckException(StatusEnum status, Code code, Throwable e) {
        super(status, code, e);
    }

    public CheckException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public CheckException(StatusEnum status, Code code, Throwable e, String message) {
        super(status, code, e, message);
    }
}

