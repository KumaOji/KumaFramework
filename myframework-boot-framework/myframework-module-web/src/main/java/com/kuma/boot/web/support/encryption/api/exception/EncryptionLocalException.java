/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.support.encryption.api.exception;

public class EncryptionLocalException
extends RuntimeException {
    public EncryptionLocalException() {
    }

    public EncryptionLocalException(String message) {
        super(message);
    }

    public EncryptionLocalException(String message, Throwable cause) {
        super(message, cause);
    }

    public EncryptionLocalException(Throwable cause) {
        super(cause);
    }

    public EncryptionLocalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

