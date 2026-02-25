/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.enums.ResultEnum
 *  com.kuma.boot.common.enums.StatusEnum
 *  com.kuma.boot.common.exception.BootException
 *  com.kuma.boot.common.model.Code
 */
package com.kuma.boot.idempotent.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class IdempotentException
extends BootException {
    public IdempotentException() {
    }

    public IdempotentException(String message) {
        super(message);
    }

    public IdempotentException(Throwable e) {
        super(e);
    }

    public IdempotentException(String message, Throwable e) {
        super(message, e);
    }

    public IdempotentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public IdempotentException(ResultEnum result) {
        super(result);
    }

    public IdempotentException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public IdempotentException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public IdempotentException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public IdempotentException(Code code, String message) {
        super(code, message);
    }

    public IdempotentException(StatusEnum status, Code code, String message) {
        super(status, code, message);
    }

    public IdempotentException(Code code, Throwable e) {
        super(code, e);
    }

    public IdempotentException(StatusEnum status, Code code, Throwable e) {
        super(status, code, e);
    }

    public IdempotentException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public IdempotentException(StatusEnum status, Code code, Throwable e, String message) {
        super(status, code, e, message);
    }
}

