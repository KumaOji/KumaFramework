/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.exception;

import com.kuma.boot.dingtalk.entity.ExceptionPairs;
import com.kuma.boot.dingtalk.enums.ExceptionEnum;

public class AsyncCallException
extends DingerException {
    public AsyncCallException(String msg) {
        super(msg, (ExceptionPairs)ExceptionEnum.ASYNC_CALL);
    }

    public AsyncCallException(Throwable cause) {
        super(cause, (ExceptionPairs)ExceptionEnum.ASYNC_CALL);
    }
}

