/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.async.executor.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class SystemClock {
    private final int period;
    private final AtomicLong now;

    private SystemClock(int period) {
        this.period = period;
        this.now = new AtomicLong(System.currentTimeMillis());
        this.scheduleClockUpdating();
    }

    private static SystemClock instance() {
        return InstanceHolder.INSTANCE;
    }

    private void scheduleClockUpdating() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "System Clock");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(() -> this.now.set(System.currentTimeMillis()), this.period, this.period, TimeUnit.MILLISECONDS);
    }

    private long currentTimeMillis() {
        return this.now.get();
    }

    public static long now() {
        return SystemClock.instance().currentTimeMillis();
    }

    private static class InstanceHolder {
        private static final SystemClock INSTANCE = new SystemClock(1);

        private InstanceHolder() {
        }
    }
}

