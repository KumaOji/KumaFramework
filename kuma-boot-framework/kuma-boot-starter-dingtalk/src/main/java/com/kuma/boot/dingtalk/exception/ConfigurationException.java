/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.exception;

import com.kuma.boot.dingtalk.entity.ExceptionPairs;
import com.kuma.boot.dingtalk.enums.ExceptionEnum;

public class ConfigurationException
extends DingerException {
    public ConfigurationException(String msg) {
        super(msg, (ExceptionPairs)ExceptionEnum.CONFIG_ERROR);
    }

    public ConfigurationException(Throwable cause) {
        super(cause, (ExceptionPairs)ExceptionEnum.CONFIG_ERROR);
    }
}

