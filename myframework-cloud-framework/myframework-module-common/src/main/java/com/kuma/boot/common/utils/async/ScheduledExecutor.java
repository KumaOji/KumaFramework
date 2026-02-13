/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.async;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface ScheduledExecutor
extends Executor {
    public ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4);

    public <V> ScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4);

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6);

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6);
}

