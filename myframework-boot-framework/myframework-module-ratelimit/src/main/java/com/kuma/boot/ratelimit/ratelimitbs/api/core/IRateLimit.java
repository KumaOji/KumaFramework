/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.api.core;

public interface IRateLimit {
    public boolean tryAcquire(IRateLimitContext var1);
}

