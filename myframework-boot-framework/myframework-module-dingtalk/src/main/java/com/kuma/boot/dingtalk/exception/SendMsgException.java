/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.exception;

import com.kuma.boot.dingtalk.entity.ExceptionPairs;
import com.kuma.boot.dingtalk.enums.ExceptionEnum;

public class SendMsgException
extends DingerException {
    public SendMsgException(String msg) {
        super(msg, (ExceptionPairs)ExceptionEnum.SEND_MSG);
    }

    public SendMsgException(Throwable cause) {
        super(cause, (ExceptionPairs)ExceptionEnum.SEND_MSG);
    }
}

