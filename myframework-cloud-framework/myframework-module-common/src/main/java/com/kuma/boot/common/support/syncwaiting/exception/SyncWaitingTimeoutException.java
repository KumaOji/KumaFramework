/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.syncwaiting.exception;

import com.kuma.boot.common.support.syncwaiting.exception.SyncWaitingException;

public class SyncWaitingTimeoutException
extends SyncWaitingException {
    public SyncWaitingTimeoutException(String message) {
        super(message);
    }

    public SyncWaitingTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public SyncWaitingTimeoutException(Throwable cause) {
        super(cause);
    }
}

