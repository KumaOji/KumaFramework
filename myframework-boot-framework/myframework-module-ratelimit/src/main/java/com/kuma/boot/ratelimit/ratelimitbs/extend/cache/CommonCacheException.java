/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.extend.cache;

public class CommonCacheException
extends RuntimeException {
    public CommonCacheException() {
    }

    public CommonCacheException(String message) {
        super(message);
    }

    public CommonCacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonCacheException(Throwable cause) {
        super(cause);
    }

    public CommonCacheException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

