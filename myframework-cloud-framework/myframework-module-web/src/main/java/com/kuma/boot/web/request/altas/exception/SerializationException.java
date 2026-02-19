/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.request.altas.exception;

public class SerializationException
extends LogException {
    private static final long serialVersionUID = 1L;
    private final Class<?> objectType;

    public SerializationException(Class<?> objectType, String message) {
        super("\u5e8f\u5217\u5316\u5931\u8d25: " + (objectType != null ? objectType.getSimpleName() : "unknown") + ", \u539f\u56e0: " + message);
        this.objectType = objectType;
    }

    public SerializationException(Class<?> objectType, String message, Throwable cause) {
        super("\u5e8f\u5217\u5316\u5931\u8d25: " + (objectType != null ? objectType.getSimpleName() : "unknown") + ", \u539f\u56e0: " + message, cause);
        this.objectType = objectType;
    }

    public SerializationException(Class<?> objectType, Throwable cause) {
        super("\u5e8f\u5217\u5316\u5931\u8d25: " + (objectType != null ? objectType.getSimpleName() : "unknown"), cause);
        this.objectType = objectType;
    }

    public Class<?> getObjectType() {
        return this.objectType;
    }
}

