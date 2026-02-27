/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.exception;

import com.kuma.boot.dingtalk.entity.ExceptionPairs;
import com.kuma.boot.dingtalk.enums.ExceptionEnum;

public class MsgTypeException
extends DingerException {
    public MsgTypeException(String msg) {
        super(msg, (ExceptionPairs)ExceptionEnum.MSG_TYPE_CHECK);
    }

    public MsgTypeException(Throwable cause) {
        super(cause, (ExceptionPairs)ExceptionEnum.MSG_TYPE_CHECK);
    }
}

