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

/**
 * OverloadRejectedVirtualExecutor
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class OverloadRejectedVirtualExecutor implements ExecutorService {

    private static final Logger log =
            LoggerFactory.getLogger(OverloadRejectedVirtualExecutor.class);

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    private volatile boolean overload = false;

    private volatile boolean terminated = false;

    public OverloadRejectedVirtualExecutor() {
        Thread.ofVirtual()
                .name("OverloadMonitor")
                .start(
                        () -> {
                            int hb = 0;
                            int cpuOverloadCount = 0;
                            int memoryOverloadCount = 0;
                            while (!terminated) {
                                try {
                                    TimeUnit.SECONDS.sleep(1);
                                } catch (InterruptedException e) {
                                    log.error("overload rejected virtual executor sleep error", e);
                                    overload = false;
                                }
                                if (++hb >= 60) {
                                    hb = 0;
                                    // logEvent("DUBBO_VIRTUAL_THREAD_POOL", "heartbeat");
                                }

                                // 连续5s CPU Load > 0.99，设置为过载
                                var operatingSystem = ManagementFactory.getOperatingSystemMXBean();
                                if (operatingSystem
                                        instanceof
                                        OperatingSystemMXBean osBean) {
                                    double cpuLoad = osBean.getCpuLoad();
                                    double processCpuLoad = osBean.getProcessCpuLoad();
                                    if (processCpuLoad > 0.99) {
                                        overload = ++cpuOverloadCount > 4;
                                        // logEvent("DUBBO_VIRTUAL_THREAD_POOL",
                                        // "CPU_Load_over_99%_" + cpuOverloadCount);
                                        log.info(
                                                "CPU_Load: {}% , Process_CPU_Load: {}%",
                                                cpuLoad * 100, processCpuLoad * 100);
                                    }
                                    if (overload) {
                                        // logEvent("DUBBO_VIRTUAL_THREAD_POOL", "overload");
                                        continue;
                                    }
                                }
                                cpuOverloadCount = 0;

                                // 连续5s Memory Used > 99%，设置为过载
                                var usedMemory =
                                        Runtime.getRuntime().totalMemory()
                                                - Runtime.getRuntime().freeMemory();
                                var maxMemory = Runtime.getRuntime().maxMemory();
                                if (100d * usedMemory / maxMemory > 99) {
                                    overload = ++memoryOverloadCount > 4;
                                    // logEvent("DUBBO_VIRTUAL_THREAD_POOL", "Memory_Used_over_99%_"
                                    // + memoryOverloadCount);
                                }
                                if (overload) {
                                    // logEvent("DUBBO_VIRTUAL_THREAD_POOL", "overload");
                                    continue;
                                }
                                memoryOverloadCount = 0;

                                overload = false;
                            }
                        });
        log.info("dubbo virtual thread executor init.");
    }

    private void reject() {
        RejectedExecutionException e =
                new RejectedExecutionException("Dubbo server is overload now!");
        // logError(e);
        throw e;
    }

    @Override
    public void execute( Runnable command ) {
        if (overload) {
            reject();
        }
        executor.execute(command);
    }

    @Override
    public void close() {
        terminated = true;
        executor.close();
    }

    @Override
    public <T> Future<T> submit( Callable<T> task ) {
        if (overload) {
            reject();
        }
        return executor.submit(task);
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }

    @NotNull
    @Override
    public List<Runnable> shutdownNow() {
        return executor.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return executor.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return executor.isTerminated();
    }

    @Override
    public boolean awaitTermination( long timeout, @NotNull TimeUnit unit )
            throws InterruptedException {
        return executor.awaitTermination(timeout, unit);
    }

    @NotNull
    @Override
    public <T> Future<T> submit( @NotNull Runnable task, T result ) {
        if (overload) {
            reject();
        }
        return executor.submit(task, result);
    }

    @NotNull
    @Override
    public Future<?> submit( @NotNull Runnable task ) {
        if (overload) {
            reject();
        }
        return executor.submit(task);
    }

    @NotNull
    @Override
    public <T> List<Future<T>> invokeAll( @NotNull Collection<? extends Callable<T>> tasks )
            throws InterruptedException {
        if (overload) {
            reject();
        }
        return executor.invokeAll(tasks);
    }

    @NotNull
    @Override
    public <T> List<Future<T>> invokeAll(
            @NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit )
            throws InterruptedException {
        if (overload) {
            reject();
        }
        return executor.invokeAll(tasks, timeout, unit);
    }

    @NotNull
    @Override
    public <T> T invokeAny( @NotNull Collection<? extends Callable<T>> tasks )
            throws InterruptedException, ExecutionException {
        if (overload) {
            reject();
        }
        return executor.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(
            @NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit )
            throws InterruptedException, ExecutionException, TimeoutException {
        if (overload) {
            reject();
        }
        return executor.invokeAny(tasks, timeout, unit);
    }
}
