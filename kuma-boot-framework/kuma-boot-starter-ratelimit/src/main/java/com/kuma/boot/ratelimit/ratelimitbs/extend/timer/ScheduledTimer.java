/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.ratelimit.ratelimitbs.extend.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ScheduledTimer implements com.kuma.boot.ratelimit.ratelimitbs.extend.timer.ITimer {
    private static final ScheduledTimer INSTANCE = new ScheduledTimer();
    private final AtomicLong atomicLong;

    public ScheduledTimer(long currentTimeMillis, final long periodMills) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        this.atomicLong = new AtomicLong(currentTimeMillis);
        executor.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        ScheduledTimer.this.atomicLong.addAndGet(periodMills);
                    }
                },
                0L,
                periodMills,
                TimeUnit.MILLISECONDS);
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

    public long time() {
        return this.atomicLong.get();
    }
}
