/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.aop.aop.limiting;

public class RateLimitedException
extends RuntimeException {
    public RateLimitedException(String message) {
        super(message);
    }
}

