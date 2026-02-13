/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
 */
package com.kuma.boot.common.support.thread;

import com.kuma.boot.common.support.thread.ThreadPoolUncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class ThreadPoolFactory
implements ThreadFactory {
    private final AtomicInteger poolNumber = new AtomicInteger(1);
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final boolean daemon;
    private final ThreadGroup group;
    private final ThreadPoolTaskExecutor executor;

    public ThreadPoolFactory(String namePrefix, boolean daemon, ThreadPoolTaskExecutor executor) {
        this.daemon = daemon;
        this.executor = executor;
        this.group = executor != null ? executor.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix + "-pool-" + this.poolNumber.getAndIncrement();
    }

    public ThreadPoolFactory(String namePrefix, ThreadPoolTaskExecutor executor) {
        this(namePrefix, false, executor);
    }

    public ThreadPoolFactory(String namePrefix) {
        this(namePrefix, false, null);
    }

    public ThreadPoolFactory(String namePrefix, boolean daemon) {
        this(namePrefix, daemon, null);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(this.group, r, this.namePrefix + "-thread-" + this.threadNumber.getAndIncrement(), 0L);
        Thread.UncaughtExceptionHandler handler = t.getUncaughtExceptionHandler();
        if (!(handler instanceof ThreadPoolUncaughtExceptionHandler)) {
            t.setUncaughtExceptionHandler(new ThreadPoolUncaughtExceptionHandler(handler));
        }
        if (t.getPriority() != 5) {
            t.setPriority(5);
        }
        t.setDaemon(this.daemon);
        return t;
    }

    public String getNamePrefix() {
        return this.namePrefix;
    }

    public AtomicInteger getPoolNumber() {
        return this.poolNumber;
    }

    public AtomicInteger getThreadNumber() {
        return this.threadNumber;
    }

    public ThreadPoolTaskExecutor getExecutor() {
        return this.executor;
    }
}

