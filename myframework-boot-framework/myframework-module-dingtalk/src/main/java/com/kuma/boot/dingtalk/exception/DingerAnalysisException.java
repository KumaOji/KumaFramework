/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.exception;

import com.kuma.boot.dingtalk.entity.ExceptionPairs;

public class DingerAnalysisException
extends DingerException {
    public DingerAnalysisException(ExceptionPairs pairs) {
        super(pairs);
    }

    public DingerAnalysisException(ExceptionPairs pairs, String message) {
        super(message, pairs);
    }
}

