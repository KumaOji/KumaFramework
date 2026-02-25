/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.idempotent.idempotentenhance.core.handler;

import com.kuma.boot.idempotent.idempotentenhance.core.handler.event.IdempotentExceptionEvent;

public interface IdempotentExceptionEventHandler {
    public void handle(IdempotentExceptionEvent var1);
}

