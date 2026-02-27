/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.exception;

import com.kuma.boot.dingtalk.entity.ExceptionPairs;
import com.kuma.boot.dingtalk.enums.ExceptionEnum;

public class InvalidPropertiesFormatException
extends DingerException {
    public InvalidPropertiesFormatException(String msg) {
        super(msg, (ExceptionPairs)ExceptionEnum.PROPERTIES_ERROR);
    }

    public InvalidPropertiesFormatException(Throwable cause) {
        super(cause, (ExceptionPairs)ExceptionEnum.PROPERTIES_ERROR);
    }
}

