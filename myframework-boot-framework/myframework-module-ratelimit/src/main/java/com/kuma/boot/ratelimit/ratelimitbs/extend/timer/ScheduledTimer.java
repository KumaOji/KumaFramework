/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.extend.timer;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ScheduledTimer
implements ITimer {
    private static final ScheduledTimer INSTANCE = new ScheduledTimer();
    private final AtomicLong atomicLong;

    public ScheduledTimer(long currentTimeMillis, final long periodMills) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        this.atomicLong = new AtomicLong(currentTimeMillis);
        executor.scheduleAtFixedRate(new Runnable(){
            final /* synthetic */ ScheduledTimer this$0;
            {
                ScheduledTimer scheduledTimer = this$0;
                Objects.requireNonNull(scheduledTimer);
                this.this$0 = scheduledTimer;
            }

            @Override
            public void run() {
                this.this$0.atomicLong.addAndGet(periodMills);
            }
        }, 0L, periodMills, TimeUnit.MILLISECONDS);
    }

    public ScheduledTimer(long currentTimeMillis) {
        this(currentTimeMillis, 1L);
    }

    private ScheduledTimer() {
        this(System.currentTimeMillis());
    }

    public static ScheduledTimer getInstance() {
        return INSTANCE;
    }

    @Override
    public long time() {
        return this.atomicLong.get();
    }
}

