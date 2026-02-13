/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class LockException
extends BootException {
    public LockException() {
    }

    public LockException(String message) {
        super(message);
    }

    public LockException(Throwable e) {
        super(e);
    }

    public LockException(String message, Throwable e) {
        super(message, e);
    }

    public LockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public LockException(ResultEnum result) {
        super(result);
    }

    public LockException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public LockException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public LockException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public LockException(Code code, String message) {
        super(code, message);
    }

    public LockException(StatusEnum status, Code code, String message) {
        super(status, code, message);
    }

    public LockException(Code code, Throwable e) {
        super(code, e);
    }

    public LockException(StatusEnum status, Code code, Throwable e) {
        super(status, code, e);
    }

    public LockException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public LockException(StatusEnum status, Code code, Throwable e, String message) {
        super(status, code, e, message);
    }
}

