/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.repository.exception;

import com.kuma.boot.security.spring.enums.ErrorCodeEnum;

public class UpdateConnectionException
extends RuntimeException {
    private static final long serialVersionUID = 620L;
    private final ErrorCodeEnum errorCodeEnum;
    private final Object data;

    public UpdateConnectionException(ErrorCodeEnum errorCodeEnum, Object data) {
        super(errorCodeEnum.getMsg());
        this.errorCodeEnum = errorCodeEnum;
        this.data = data;
    }

    public UpdateConnectionException(ErrorCodeEnum errorCodeEnum, Object data, Throwable cause) {
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

