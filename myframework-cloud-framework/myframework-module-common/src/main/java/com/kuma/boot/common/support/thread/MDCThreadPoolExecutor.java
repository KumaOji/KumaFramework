/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.ttl.TtlCallable
 *  com.alibaba.ttl.TtlRunnable
 */
package com.kuma.boot.common.support.thread;

import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import com.kuma.boot.common.support.thread.MDCCallable;
import com.kuma.boot.common.support.thread.MDCRunnable;
import com.kuma.boot.common.support.thread.ThreadPoolFactory;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MDCThreadPoolExecutor
extends ThreadPoolExecutor {
    public MDCThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public MDCThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public MDCThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public MDCThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    public void execute(Runnable runnable) {
        TtlRunnable ttlRunnable = TtlRunnable.get((Runnable)runnable);
        this.showThreadPoolInfo("execute(Runnable task)");
        super.execute(new MDCRunnable((Runnable)ttlRunnable));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        TtlCallable ttlCallable = TtlCallable.get(task);
        this.showThreadPoolInfo("submit(Callable<T> task)");
        return super.submit(new MDCCallable(ttlCallable));
    }

    @Override
    public Future<?> submit(Runnable task) {
        TtlRunnable ttlRunnable = TtlRunnable.get((Runnable)task);
        this.showThreadPoolInfo("submit(Runnable task)");
        return super.submit(new MDCRunnable((Runnable)ttlRunnable));
    }

    private void showThreadPoolInfo(String method) {
        ThreadFactory threadFactory = this.getThreadFactory();
        ThreadPoolFactory threadPoolFactory = null;
        if (threadFactory instanceof ThreadPoolFactory) {
            threadPoolFactory = (ThreadPoolFactory)threadFactory;
        }
        String threadNamePrefix = Objects.nonNull(threadPoolFactory) ? threadPoolFactory.getNamePrefix() : Thread.currentThread().getName();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement stackTraceElement = stackTrace[stackTrace.length - 2];
    }
}

