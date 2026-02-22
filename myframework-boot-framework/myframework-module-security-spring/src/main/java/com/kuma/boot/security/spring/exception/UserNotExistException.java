/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.exception;

import com.kuma.boot.security.spring.enums.ErrorCodeEnum;

public class UserNotExistException
extends AbstractResponseJsonAuthenticationException {
    private static final long serialVersionUID = 3042211783958201322L;

    public UserNotExistException(ErrorCodeEnum errorCodeEnum, String userId) {
        super(errorCodeEnum, null, userId);
    }

    public UserNotExistException(ErrorCodeEnum errorCodeEnum, Throwable cause, String userId) {
        super(errorCodeEnum, cause, null, userId);
    }
}

