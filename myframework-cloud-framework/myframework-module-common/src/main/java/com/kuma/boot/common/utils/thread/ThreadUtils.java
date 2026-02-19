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

package com.kuma.boot.common.utils.thread;

import java.util.Objects;
import java.util.concurrent.*;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;

/**
 * 线程工具类
 */
public final class ThreadUtils {

    /**
     * 获取 cpu 数量
     * @return cpu 数量
     */
    private static int cpuNum() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * 最佳线程数量
     * @return 线程数量
     */
    public static int bestThreadNum() {
        int cpuNum = cpuNum();
        return cpuNum * 3;
    }

    /**
     * 最佳线程数量 1. 如果目标值较小，则返回较少的即可。
     * @param targetSize 目标大小
     * @return 线程数量
     */
    public static int bestThreadNum(final int targetSize) {
        int bestNum = bestThreadNum();
        if (targetSize < bestNum) {
            return targetSize;
        }
        return bestNum;
    }

    private ThreadUtils() {}

    private static final int THREAD_MULTIPLER = 2;

    /**
     * Wait.
     * @param object load object
     */
    public static void objectWait(Object object) {
        try {
            object.wait();
        } catch (InterruptedException ignore) {
            Thread.interrupted();
        }
    }

    /**
     * Sleep.
     * @param millis sleep millisecond
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void countDown(CountDownLatch latch) {
        Objects.requireNonNull(latch, "latch");
        latch.countDown();
    }

    /**
     * Await count down latch.
     * @param latch count down latch
     */
    public static void latchAwait(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Await count down latch with timeout.
     * @param latch count down latch
     * @param time timeout time
     * @param unit time unit
     */
    public static void latchAwait(CountDownLatch latch, long time, TimeUnit unit) {
        try {
            latch.await(time, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static int getSuitableThreadCount() {
        return getSuitableThreadCount(THREAD_MULTIPLER);
    }

    public static int getSuitableThreadCount(int threadMultiple) {
        final int coreCount = getProcessorsCount();
        int workerCount = 1;
        while (workerCount < coreCount * threadMultiple) {
            workerCount <<= 1;
        }
        return workerCount;
    }

    public static void shutdownThreadPool(ExecutorService executor) {
        shutdownThreadPool(executor, null);
    }

    /**
     * Shutdown thread pool.
     * @param executor thread pool
     * @param logger logger
     */
    public static void shutdownThreadPool(ExecutorService executor, Logger logger) {
        if (executor == null) {
            return;
        }
        executor.shutdown();
        int retry = 3;
        while (retry > 0) {
            retry--;
            try {
                if (executor.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                    return;
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();

                Thread.interrupted();
            } catch (Throwable ex) {
                if (logger != null) {
                    logger.error("ThreadPoolManager shutdown executor has error : ", ex);
                }
            }
        }
        executor.shutdownNow();
    }

    public static void addShutdownHook(Runnable runnable) {
        Runtime.getRuntime().addShutdownHook(new Thread(runnable));
    }

    private static final String PROCESSORS_ENV_NAME = "NACOS_COMMON_PROCESSORS";

    private static final String PROCESSORS_PROP_NAME = "nacos.common.processors";

    public static String getProperty(String propertyName, String envName) {
        return System.getenv().getOrDefault(envName, System.getProperty(propertyName));
    }

    public static String getProperty(String propertyName, String envName, String defaultValue) {
        return System.getenv()
                .getOrDefault(envName, System.getProperty(propertyName, defaultValue));
    }

    public static int getProcessorsCount() {
        int processorsCount = 0;
        String processorsCountPreSet = getProperty(PROCESSORS_PROP_NAME, PROCESSORS_ENV_NAME);
        if (processorsCountPreSet != null) {
            try {
                processorsCount = Integer.parseInt(processorsCountPreSet);
            } catch (NumberFormatException ignored) {
            }
        }
        if (processorsCount <= 0) {
            processorsCount = Runtime.getRuntime().availableProcessors();
        }
        return processorsCount;
    }

    /**
     * 新建一个虚拟线程池.
     */
    public static ExecutorService newVirtualTaskExecutor() {
        return Executors.newThreadPerTaskExecutor(VirtualThreadFactory.INSTANCE);
    }

    public static ExecutorService newTtlVirtualTaskExecutor() {
        return TtlExecutors.getTtlExecutorService(newVirtualTaskExecutor());
    }

    public static ScheduledExecutorService newScheduledThreadPool(int coreSize) {
        return Executors.newScheduledThreadPool(coreSize,
                new ThreadFactoryBuilder().setNameFormat("iot-scheduled-").build());
    }
}
