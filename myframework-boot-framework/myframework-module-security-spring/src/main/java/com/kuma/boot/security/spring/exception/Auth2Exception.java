/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.core.AuthenticationException
 */
package com.kuma.boot.security.spring.exception;

import com.kuma.boot.security.spring.enums.ErrorCodeEnum;
import org.springframework.security.core.AuthenticationException;

public class Auth2Exception
extends AuthenticationException {
    private final ErrorCodeEnum errorCodeEnum;
    private final Object data;

    public Auth2Exception(ErrorCodeEnum errorCodeEnum, Object data) {
        super(errorCodeEnum.getMsg());
        this.errorCodeEnum = errorCodeEnum;
        this.data = data;
    }

    public Auth2Exception(ErrorCodeEnum errorCodeEnum, Object data, Throwable cause) {
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

