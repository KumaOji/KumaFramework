/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  io.github.itning.retry.Retryer
 *  io.github.itning.retry.RetryerBuilder
 *  io.github.itning.retry.strategy.stop.StopStrategies
 *  io.github.itning.retry.strategy.wait.WaitStrategies
 */
package com.kuma.boot.web.support.methodPartAndRetryer;

import io.github.itning.retry.Retryer;
import io.github.itning.retry.RetryerBuilder;
import io.github.itning.retry.strategy.stop.StopStrategies;
import io.github.itning.retry.strategy.wait.WaitStrategies;
import java.util.concurrent.TimeUnit;

public class RetryUtil<V> {
    public Retryer<V> getDefaultRetryer(int times, long waitTime) {
        Retryer retryer = RetryerBuilder.newBuilder().retryIfException().retryIfRuntimeException().retryIfExceptionOfType(Exception.class).withWaitStrategy(WaitStrategies.fixedWait((long)waitTime, (TimeUnit)TimeUnit.MILLISECONDS)).withStopStrategy(StopStrategies.stopAfterAttempt((int)times)).build();
        return retryer;
    }
}

