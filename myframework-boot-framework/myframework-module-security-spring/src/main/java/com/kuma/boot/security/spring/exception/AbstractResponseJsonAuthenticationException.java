/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.core.AuthenticationException
 */
package com.kuma.boot.security.spring.exception;

import com.kuma.boot.security.spring.enums.ErrorCodeEnum;
import org.springframework.security.core.AuthenticationException;

public abstract class AbstractResponseJsonAuthenticationException
extends AuthenticationException {
    private static final long serialVersionUID = 2661098918363948470L;
    protected ErrorCodeEnum errorCodeEnum;
    protected Object data;
    protected String uid;

    public AbstractResponseJsonAuthenticationException(ErrorCodeEnum errorCodeEnum, Throwable t, Object data, String uid) {
        super(errorCodeEnum.getMsg(), t);
        this.errorCodeEnum = errorCodeEnum;
        this.data = data;
        this.uid = uid;
    }

    public AbstractResponseJsonAuthenticationException(ErrorCodeEnum errorCodeEnum, Object data, String uid) {
        super(errorCodeEnum.getMsg());
        this.errorCodeEnum = errorCodeEnum;
        this.data = data;
        this.uid = uid;
    }

    public ErrorCodeEnum getErrorCodeEnum() {
        return this.errorCodeEnum;
    }

    public Object getData() {
        return this.data;
    }

    public String getUid() {
        return this.uid;
    }
}

