/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.core;

import com.kuma.boot.ratelimit.ratelimitbs.api.core.IRateLimit;

public final class RateLimits {
    private RateLimits() {
    }

    public static IRateLimit fixedWindow() {
        return new RateLimitFixedWindow();
    }

    public static IRateLimit slideWindow() {
        return new RateLimitSlideWindow();
    }

    public static IRateLimit slideWindow(int windowNum) {
        return new RateLimitSlideWindow(windowNum);
    }

    public static IRateLimit slideWindowQueue() {
        return new RateLimitSlideWindowQueue();
    }

    public static IRateLimit leakyBucket() {
        return new RateLimitLeakyBucket();
    }

    public static IRateLimit tokenBucket() {
        return new RateLimitTokenBucket();
    }
}

