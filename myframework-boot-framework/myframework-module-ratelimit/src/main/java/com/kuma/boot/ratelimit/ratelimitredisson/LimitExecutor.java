/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitredisson;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public interface LimitExecutor {
    @Deprecated(since="1.3.5")
    default public boolean tryAccess(String compositeKey, int rate, int rateInterval, TimeUnit unit) {
        return this.tryAccess(compositeKey, rate, Duration.ofMillis(unit.toMillis(rateInterval)));
    }

    public boolean tryAccess(String var1, int var2, Duration var3);
}

