/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.handler;

import com.kuma.boot.common.support.handler.Handler;
import com.kuma.boot.common.utils.lang.ObjectUtils;

public abstract class AbstractHandler<T, R>
implements Handler<T, R> {
    @Override
    public R handle(T t) {
        if (ObjectUtils.isNull(t)) {
            return null;
        }
        return this.doHandle(t);
    }

    protected abstract R doHandle(T var1);
}

