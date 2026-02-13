/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  jakarta.annotation.Nullable
 */
package com.kuma.boot.common.utils.async;

import com.google.common.base.Preconditions;
import com.kuma.boot.common.utils.async.FatalExitExceptionHandler;
import jakarta.annotation.Nullable;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorThreadFactory
implements ThreadFactory {
    private static final String DEFAULT_POOL_NAME = "flink-executor-pool";
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final String namePrefix;
    private final int threadPriority;
    @Nullable
    private final Thread.UncaughtExceptionHandler exceptionHandler;

    public ExecutorThreadFactory() {
        this(DEFAULT_POOL_NAME);
    }

    public ExecutorThreadFactory(String poolName) {
        this(poolName, FatalExitExceptionHandler.INSTANCE);
    }

    public ExecutorThreadFactory(String poolName, Thread.UncaughtExceptionHandler exceptionHandler) {
        this(poolName, 5, exceptionHandler);
    }

    ExecutorThreadFactory(String poolName, int threadPriority, @Nullable Thread.UncaughtExceptionHandler exceptionHandler) {
        this.namePrefix = (String)Preconditions.checkNotNull((Object)poolName, (Object)"poolName") + "-thread-";
        this.threadPriority = threadPriority;
        this.exceptionHandler = exceptionHandler;
        this.group = Thread.currentThread().getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread t = new Thread(this.group, runnable, this.namePrefix + this.threadNumber.getAndIncrement());
        t.setDaemon(true);
        t.setPriority(this.threadPriority);
        if (this.exceptionHandler != null) {
            t.setUncaughtExceptionHandler(this.exceptionHandler);
        }
        return t;
    }

    public static final class Builder {
        private String poolName;
        private int priority = 5;
        private Thread.UncaughtExceptionHandler exceptionHandler = FatalExitExceptionHandler.INSTANCE;

        public Builder setPoolName(String poolName) {
            this.poolName = poolName;
            return this;
        }

        public Builder setThreadPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder setExceptionHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        public ExecutorThreadFactory build() {
            return new ExecutorThreadFactory(this.poolName, this.priority, this.exceptionHandler);
        }
    }
}

