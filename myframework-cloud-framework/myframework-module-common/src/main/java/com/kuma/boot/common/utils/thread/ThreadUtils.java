/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.ttl.threadpool.TtlExecutors
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  org.slf4j.Logger
 */
package com.kuma.boot.common.utils.thread;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kuma.boot.common.utils.thread.VirtualThreadFactory;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;

public final class ThreadUtils {
    private static final int THREAD_MULTIPLER = 2;
    private static final String PROCESSORS_ENV_NAME = "NACOS_COMMON_PROCESSORS";
    private static final String PROCESSORS_PROP_NAME = "nacos.common.processors";

    private static int cpuNum() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static int bestThreadNum() {
        int cpuNum = ThreadUtils.cpuNum();
        return cpuNum * 3;
    }

    public static int bestThreadNum(int targetSize) {
        int bestNum = ThreadUtils.bestThreadNum();
        if (targetSize < bestNum) {
            return targetSize;
        }
        return bestNum;
    }

    private ThreadUtils() {
    }

    public static void objectWait(Object object) {
        try {
            object.wait();
        }
        catch (InterruptedException ignore) {
            Thread.interrupted();
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void countDown(CountDownLatch latch) {
        Objects.requireNonNull(latch, "latch");
        latch.countDown();
    }

    public static void latchAwait(CountDownLatch latch) {
        try {
            latch.await();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void latchAwait(CountDownLatch latch, long time, TimeUnit unit) {
        try {
            latch.await(time, unit);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static int getSuitableThreadCount() {
        return ThreadUtils.getSuitableThreadCount(2);
    }

    public static int getSuitableThreadCount(int threadMultiple) {
        int workerCount;
        int coreCount = ThreadUtils.getProcessorsCount();
        for (workerCount = 1; workerCount < coreCount * threadMultiple; workerCount <<= 1) {
        }
        return workerCount;
    }

    public static void shutdownThreadPool(ExecutorService executor) {
        ThreadUtils.shutdownThreadPool(executor, null);
    }

    public static void shutdownThreadPool(ExecutorService executor, Logger logger) {
        if (executor == null) {
            return;
        }
        executor.shutdown();
        for (int retry = 3; retry > 0; --retry) {
            try {
                if (!executor.awaitTermination(100L, TimeUnit.MILLISECONDS)) continue;
                return;
            }
            catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.interrupted();
                continue;
            }
            catch (Throwable ex) {
                if (logger == null) continue;
                logger.error("ThreadPoolManager shutdown executor has error : ", ex);
                continue;
            }
        }
        executor.shutdownNow();
    }

    public static void addShutdownHook(Runnable runnable) {
        Runtime.getRuntime().addShutdownHook(new Thread(runnable));
    }

    public static String getProperty(String propertyName, String envName) {
        return System.getenv().getOrDefault(envName, System.getProperty(propertyName));
    }

    public static String getProperty(String propertyName, String envName, String defaultValue) {
        return System.getenv().getOrDefault(envName, System.getProperty(propertyName, defaultValue));
    }

    public static int getProcessorsCount() {
        int processorsCount = 0;
        String processorsCountPreSet = ThreadUtils.getProperty(PROCESSORS_PROP_NAME, PROCESSORS_ENV_NAME);
        if (processorsCountPreSet != null) {
            try {
                processorsCount = Integer.parseInt(processorsCountPreSet);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        if (processorsCount <= 0) {
            processorsCount = Runtime.getRuntime().availableProcessors();
        }
        return processorsCount;
    }

    public static ExecutorService newVirtualTaskExecutor() {
        return Executors.newThreadPerTaskExecutor(VirtualThreadFactory.INSTANCE);
    }

    public static ExecutorService newTtlVirtualTaskExecutor() {
        return TtlExecutors.getTtlExecutorService((ExecutorService)ThreadUtils.newVirtualTaskExecutor());
    }

    public static ScheduledExecutorService newScheduledThreadPool(int coreSize) {
        return Executors.newScheduledThreadPool(coreSize, new ThreadFactoryBuilder().setNameFormat("iot-scheduled-").build());
    }
}

