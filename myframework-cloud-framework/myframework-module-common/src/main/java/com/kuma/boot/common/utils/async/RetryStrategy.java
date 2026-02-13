/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.async;

import java.time.Duration;

public interface RetryStrategy {
    public int getNumRemainingRetries();

    public Duration getRetryDelay();

    public RetryStrategy getNextRetryStrategy();
}

