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

package com.kuma.boot.core.support.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.kuma.boot.common.support.thread.ThreadPoolUncaughtExceptionHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * ThreadPoolFactory
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class ThreadPoolFactory implements ThreadFactory {

    private final AtomicInteger poolNumber = new AtomicInteger(1);

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final String namePrefix;

    private final boolean daemon;

    private final ThreadGroup group;

    private final ThreadPoolTaskExecutor executor;

    public ThreadPoolFactory( String namePrefix, boolean daemon, ThreadPoolTaskExecutor executor ) {
        this.daemon = daemon;
        this.executor = executor;
        this.group =
                executor != null
                        ? executor.getThreadGroup()
                        : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix + "-pool-" + poolNumber.getAndIncrement();
    }

    public ThreadPoolFactory( String namePrefix, ThreadPoolTaskExecutor executor ) {
        this(namePrefix, false, executor);
    }

    public ThreadPoolFactory( String namePrefix ) {
        this(namePrefix, false, null);
    }

    public ThreadPoolFactory( String namePrefix, boolean daemon ) {
        this(namePrefix, daemon, null);
    }

    @Override
    public Thread newThread( Runnable r ) {
        Thread t =
                new Thread(group, r, namePrefix + "-thread-" + threadNumber.getAndIncrement(), 0);

        Thread.UncaughtExceptionHandler handler = t.getUncaughtExceptionHandler();
        if (!( handler instanceof ThreadPoolUncaughtExceptionHandler)) {
            t.setUncaughtExceptionHandler(new ThreadPoolUncaughtExceptionHandler(handler));
        }

        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        t.setDaemon(daemon);

        return t;
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public AtomicInteger getPoolNumber() {
        return poolNumber;
    }

    public AtomicInteger getThreadNumber() {
        return threadNumber;
    }

    public ThreadPoolTaskExecutor getExecutor() {
        return executor;
    }
}
