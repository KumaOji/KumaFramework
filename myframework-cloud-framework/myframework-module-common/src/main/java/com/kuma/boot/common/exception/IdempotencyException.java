/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class IdempotencyException
extends BootException {
    public IdempotencyException() {
    }

    public IdempotencyException(String message) {
        super(message);
    }

    public IdempotencyException(Throwable e) {
        super(e);
    }

    public IdempotencyException(String message, Throwable e) {
        super(message, e);
    }

    public IdempotencyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public IdempotencyException(ResultEnum result) {
        super(result);
    }

    public IdempotencyException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public IdempotencyException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public IdempotencyException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public IdempotencyException(Code code, String message) {
        super(code, message);
    }

    public IdempotencyException(StatusEnum status, Code code, String message) {
        super(status, code, message);
    }

    public IdempotencyException(Code code, Throwable e) {
        super(code, e);
    }

    public IdempotencyException(StatusEnum status, Code code, Throwable e) {
        super(status, code, e);
    }

    public IdempotencyException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public IdempotencyException(StatusEnum status, Code code, Throwable e, String message) {
        super(status, code, e, message);
    }
}

