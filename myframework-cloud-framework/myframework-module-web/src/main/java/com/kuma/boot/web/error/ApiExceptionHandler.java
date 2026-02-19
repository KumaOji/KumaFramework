/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.error;

public interface ApiExceptionHandler {
    public boolean canHandle(Throwable var1);

    public ApiErrorResponse handle(Throwable var1);
}

