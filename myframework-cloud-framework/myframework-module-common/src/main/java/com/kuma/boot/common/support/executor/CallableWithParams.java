/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.executor;

import java.io.Serializable;

@FunctionalInterface
public interface CallableWithParams<R, P>
extends Serializable {
    public R call(P ... var1) throws Exception;

    default public R callWithRuntimeException(P ... params) {
        try {
            return this.call(params);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default public R callWithoutException(P ... params) {
        try {
            return this.call(params);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static <R, P> CallableWithParams<R, P> empty() {
        return params -> null;
    }
}

