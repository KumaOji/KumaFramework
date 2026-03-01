/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.exception;

import com.kuma.boot.dingtalk.entity.ExceptionPairs;

public class DingerException
extends RuntimeException {
    private ExceptionPairs pairs;

    public DingerException(ExceptionPairs pairs) {
        super((String)pairs.desc());
        this.pairs = pairs;
    }

    public DingerException(String msg, ExceptionPairs pairs) {
        super(msg);
        this.pairs = pairs;
    }

    public DingerException(ExceptionPairs pairs, Object ... msgArgs) {
        super(pairs.message(msgArgs));
        this.pairs = pairs;
    }

    public DingerException(Throwable cause, ExceptionPairs pairs) {
        super(cause);
        this.pairs = pairs;
    }

    public ExceptionPairs getPairs() {
        return this.pairs;
    }
}

