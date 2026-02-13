/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.syncwaiting.exception;

public class SyncWaitingException
extends RuntimeException {
    public SyncWaitingException(String message) {
        super(message);
    }

    public SyncWaitingException(String message, Throwable cause) {
        super(message, cause);
    }

    public SyncWaitingException(Throwable cause) {
        super(cause);
    }
}

