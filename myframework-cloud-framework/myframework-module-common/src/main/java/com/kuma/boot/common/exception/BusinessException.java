/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.model.Code;

public class BusinessException
extends BaseException {
    private static final long serialVersionUID = 6610083281801529147L;

    public BusinessException(StatusEnum status, Code code, Throwable e, String message) {
        super(status, code, e, message);
    }

    public BusinessException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public BusinessException(StatusEnum status, Code code, Throwable e) {
        super(status, code, e);
    }

    public BusinessException(Code code, Throwable e) {
        super(code, e);
    }

    public BusinessException(StatusEnum status, Code code, String message) {
        super(status, code, message);
    }

    public BusinessException(Code code, String message) {
        super(code, message);
    }

    public BusinessException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public BusinessException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public BusinessException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public BusinessException(ResultEnum result) {
        super(result);
    }

    public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BusinessException(String message, Throwable e) {
        super(message, e);
    }

    public BusinessException(Throwable e) {
        super(e);
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException() {
    }
}

