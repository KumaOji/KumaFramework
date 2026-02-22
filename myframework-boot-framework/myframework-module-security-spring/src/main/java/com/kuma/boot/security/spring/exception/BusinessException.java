/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.exception;

import com.kuma.boot.security.spring.enums.ErrorCodeEnum;

public abstract class BusinessException
extends RuntimeException {
    private final ErrorCodeEnum errorCodeEnum;
    private final Object data;

    public BusinessException(ErrorCodeEnum errorCodeEnum, Object data) {
        super(errorCodeEnum.getMsg());
        this.errorCodeEnum = errorCodeEnum;
        this.data = data;
    }

    public BusinessException(ErrorCodeEnum errorCodeEnum, Object data, Throwable cause) {
        super(errorCodeEnum.getMsg(), cause);
        this.errorCodeEnum = errorCodeEnum;
        this.data = data;
    }

    public ErrorCodeEnum getErrorCodeEnum() {
        return this.errorCodeEnum;
    }

    public Object getData() {
        return this.data;
    }
}

