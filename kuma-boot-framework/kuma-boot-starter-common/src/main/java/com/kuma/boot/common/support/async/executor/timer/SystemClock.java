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

package com.kuma.boot.common.support.async.executor.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用于解决高并发下System.currentTimeMillis卡顿
 *
 * @author lry
 */
public class SystemClock {

    private final int period;

    private final AtomicLong now;

    /**
     * InstanceHolder
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-17 10:30:45
     */
    private static class InstanceHolder {

        private static final SystemClock INSTANCE = new SystemClock(1);
    }

    private SystemClock( int period ) {
        this.period = period;
        this.now = new AtomicLong(System.currentTimeMillis());
        scheduleClockUpdating();
    }

    private static SystemClock instance() {
        return InstanceHolder.INSTANCE;
    }

    private void scheduleClockUpdating() {
        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor(
                        runnable -> {
                            Thread thread = new Thread(runnable, "System Clock");
                            thread.setDaemon(true);
                            return thread;
                        });
        scheduler.scheduleAtFixedRate(
                () -> now.set(System.currentTimeMillis()), period, period, TimeUnit.MILLISECONDS);
    }

    private long currentTimeMillis() {
        return now.get();
    }

    /**
     * 用来替换原来的System.currentTimeMillis()
     */
    public static long now() {
        return instance().currentTimeMillis();
    }
}
