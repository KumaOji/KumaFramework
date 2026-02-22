/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.exception;

import com.kuma.boot.security.spring.enums.ErrorCodeEnum;

public class UnBindingException
extends BusinessException {
    public UnBindingException(ErrorCodeEnum errorCodeEnum, Object data) {
        super(errorCodeEnum, data);
    }

    public UnBindingException(ErrorCodeEnum errorCodeEnum, Object data, Throwable cause) {
        super(errorCodeEnum, data, cause);
    }
}

