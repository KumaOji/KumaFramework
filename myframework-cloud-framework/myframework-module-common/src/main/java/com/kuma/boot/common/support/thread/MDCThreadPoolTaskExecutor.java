/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.ttl.TtlCallable
 *  com.alibaba.ttl.TtlRunnable
 *  org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
 */
package com.kuma.boot.common.support.thread;

import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import com.kuma.boot.common.support.thread.MDCCallable;
import com.kuma.boot.common.support.thread.MDCRunnable;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class MDCThreadPoolTaskExecutor
extends ThreadPoolTaskExecutor {
    public void execute(Runnable runnable) {
        TtlRunnable ttlRunnable = TtlRunnable.get((Runnable)runnable);
        this.showThreadPoolInfo("execute(Runnable task)");
        super.execute((Runnable)new MDCRunnable((Runnable)ttlRunnable));
    }

    public <T> Future<T> submit(Callable<T> task) {
        TtlCallable ttlCallable = TtlCallable.get(task);
        this.showThreadPoolInfo("submit(Callable<T> task)");
        return super.submit(new MDCCallable(ttlCallable));
    }

    public Future<?> submit(Runnable task) {
        TtlRunnable ttlRunnable = TtlRunnable.get((Runnable)task);
        this.showThreadPoolInfo("submit(Runnable task)");
        return super.submit((Runnable)new MDCRunnable((Runnable)ttlRunnable));
    }

    public <T> CompletableFuture<T> submitCompletable(Callable<T> task) {
        TtlCallable ttlCallable = TtlCallable.get(task);
        this.showThreadPoolInfo("submitCompletable(Runnable task)");
        return super.submitCompletable(new MDCCallable(ttlCallable));
    }

    public CompletableFuture<Void> submitCompletable(Runnable task) {
        TtlRunnable ttlRunnable = TtlRunnable.get((Runnable)task);
        this.showThreadPoolInfo("submit(Runnable task)");
        return super.submitCompletable((Runnable)new MDCRunnable((Runnable)ttlRunnable));
    }

    private void showThreadPoolInfo(String method) {
        ThreadPoolExecutor threadPoolExecutor = this.getThreadPoolExecutor();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement stackTraceElement = stackTrace[stackTrace.length - 2];
    }
}

