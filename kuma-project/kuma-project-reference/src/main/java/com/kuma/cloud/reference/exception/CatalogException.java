package com.kuma.cloud.reference.exception;

public final class CatalogException extends RuntimeException {
    public enum Reason { NOT_FOUND, UNAVAILABLE, TIMEOUT }
    private final Reason reason;

    public CatalogException(Reason reason, String message, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    public Reason reason() { return reason; }
}
