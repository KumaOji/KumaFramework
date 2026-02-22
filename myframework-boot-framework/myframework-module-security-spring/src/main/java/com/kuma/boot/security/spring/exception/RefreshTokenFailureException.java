/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.exception;

import com.kuma.boot.security.spring.enums.ErrorCodeEnum;

public class RefreshTokenFailureException
extends AbstractResponseJsonAuthenticationException {
    private static final long serialVersionUID = 6209232579710442552L;

    public RefreshTokenFailureException(ErrorCodeEnum errorCodeEnum, Throwable t, Object data, String uid) {
        super(errorCodeEnum, t, data, uid);
    }

    public RefreshTokenFailureException(ErrorCodeEnum errorCodeEnum, Object data, String uid) {
        super(errorCodeEnum, data, uid);
    }
}

