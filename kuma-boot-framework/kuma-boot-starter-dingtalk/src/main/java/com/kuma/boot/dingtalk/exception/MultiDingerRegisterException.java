/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.exception;

import com.kuma.boot.dingtalk.entity.ExceptionPairs;

public class MultiDingerRegisterException
extends DingerException {
    public MultiDingerRegisterException(ExceptionPairs pairs) {
        super(pairs);
    }

    public MultiDingerRegisterException(ExceptionPairs pairs, String message) {
        super(message, pairs);
    }
}

