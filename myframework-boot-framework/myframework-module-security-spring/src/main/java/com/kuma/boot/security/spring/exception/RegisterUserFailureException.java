/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.exception;

import com.kuma.boot.security.spring.enums.ErrorCodeEnum;

public class RegisterUserFailureException
extends AbstractResponseJsonAuthenticationException {
    private static final long serialVersionUID = 9180897671726519378L;

    public RegisterUserFailureException(ErrorCodeEnum errorCodeEnum, Throwable t, String userId) {
        super(errorCodeEnum, t, null, userId);
    }

    public RegisterUserFailureException(ErrorCodeEnum errorCodeEnum, String userId) {
        super(errorCodeEnum, null, userId);
    }
}

