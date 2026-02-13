/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.constraints.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.kuma.boot.common.support.thread;

import com.sun.management.OperatingSystemMXBean;
import jakarta.validation.constraints.NotNull;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OverloadRejectedVirtualExecutor
implements ExecutorService {
    private static final Logger log = LoggerFactory.getLogger(OverloadRejectedVirtualExecutor.class);
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private volatile boolean overload = false;
    private volatile boolean terminated = false;

    public OverloadRejectedVirtualExecutor() {
        Thread.ofVirtual().name("OverloadMonitor").start(() -> {
            int hb = 0;
            int cpuOverloadCount = 0;
            int memoryOverloadCount = 0;
            while (!this.terminated) {
                long maxMemory;
                java.lang.management.OperatingSystemMXBean operatingSystem;
                try {
                    TimeUnit.SECONDS.sleep(1L);
                }
                catch (InterruptedException e) {
                    log.error("overload rejected virtual executor sleep error", (Throwable)e);
                    this.overload = false;
                }
                if (++hb >= 60) {
                    hb = 0;
                }
                if ((operatingSystem = ManagementFactory.getOperatingSystemMXBean()) instanceof OperatingSystemMXBean) {
                    OperatingSystemMXBean osBean = (OperatingSystemMXBean)operatingSystem;
                    double cpuLoad = osBean.getCpuLoad();
                    double processCpuLoad = osBean.getProcessCpuLoad();
                    if (processCpuLoad > 0.99) {
                        this.overload = ++cpuOverloadCount > 4;
                        log.info("CPU_Load: {}% , Process_CPU_Load: {}%", (Object)(cpuLoad * 100.0), (Object)(processCpuLoad * 100.0));
                    }
                    if (this.overload) continue;
                }
                cpuOverloadCount = 0;
                long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                if (100.0 * (double)usedMemory / (double)(maxMemory = Runtime.getRuntime().maxMemory()) > 99.0) {
                    boolean bl = this.overload = ++memoryOverloadCount > 4;
                }
                if (this.overload) continue;
                memoryOverloadCount = 0;
                this.overload = false;
            }
        });
        log.info("dubbo virtual thread executor init.");
    }

    private void reject() {
        RejectedExecutionException e = new RejectedExecutionException("Dubbo server is overload now!");
        throw e;
    }

    @Override
    public void execute(Runnable command) {
        if (this.overload) {
            this.reject();
        }
        this.executor.execute(command);
    }

    @Override
    public void close() {
        this.terminated = true;
        this.executor.close();
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if (this.overload) {
            this.reject();
        }
        return this.executor.submit(task);
    }

    @Override
    public void shutdown() {
        this.executor.shutdown();
    }

    @Override
    @NotNull
    public List<Runnable> shutdownNow() {
        return this.executor.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return this.executor.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.executor.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return this.executor.awaitTermination(timeout, unit);
    }

    @Override
    @NotNull
    public <T> Future<T> submit(@NotNull Runnable task, T result) {
        if (this.overload) {
            this.reject();
        }
        return this.executor.submit(task, result);
    }

    @Override
    @NotNull
    public Future<?> submit(@NotNull Runnable task) {
        if (this.overload) {
            this.reject();
        }
        return this.executor.submit(task);
    }

    @Override
    @NotNull
    public <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException {
        if (this.overload) {
            this.reject();
        }
        return this.executor.invokeAll(tasks);
    }

    @Override
    @NotNull
    public <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        if (this.overload) {
            this.reject();
        }
        return this.executor.invokeAll(tasks, timeout, unit);
    }

    @Override
    @NotNull
    public <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        if (this.overload) {
            this.reject();
        }
        return this.executor.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (this.overload) {
            this.reject();
        }
        return this.executor.invokeAny(tasks, timeout, unit);
    }
}

