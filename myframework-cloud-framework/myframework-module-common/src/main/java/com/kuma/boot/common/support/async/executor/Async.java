/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.async.executor;

import com.kuma.boot.common.support.async.callback.DefaultGroupCallback;
import com.kuma.boot.common.support.async.callback.IGroupCallback;
import com.kuma.boot.common.support.async.wrapper.WorkerWrapper;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class Async {
    private static final ThreadPoolExecutor COMMON_POOL = (ThreadPoolExecutor)Executors.newCachedThreadPool();
    private static ExecutorService executorService;

    public static boolean beginWork(long timeout, ExecutorService executorService, List<WorkerWrapper<?, ?>> workerWrappers) throws ExecutionException, InterruptedException {
        if (workerWrappers == null || workerWrappers.isEmpty()) {
            return false;
        }
        Async.executorService = executorService;
        ConcurrentHashMap forParamUseWrappers = new ConcurrentHashMap();
        CompletableFuture[] futures = new CompletableFuture[workerWrappers.size()];
        for (int i = 0; i < workerWrappers.size(); ++i) {
            WorkerWrapper<?, ?> wrapper = workerWrappers.get(i);
            futures[i] = CompletableFuture.runAsync(() -> wrapper.work(executorService, timeout, forParamUseWrappers), executorService);
        }
        try {
            CompletableFuture.allOf(futures).get(timeout, TimeUnit.MILLISECONDS);
            return true;
        }
        catch (TimeoutException e) {
            HashSet set = new HashSet();
            Async.totalWorkers(workerWrappers, set);
            for (WorkerWrapper workerWrapper : set) {
                workerWrapper.stopNow();
            }
            return false;
        }
    }

    public static boolean beginWork(long timeout, ExecutorService executorService, WorkerWrapper<?, ?> ... workerWrapper) throws ExecutionException, InterruptedException {
        if (workerWrapper == null || workerWrapper.length == 0) {
            return false;
        }
        List<WorkerWrapper<?, ?>> workerWrappers = Arrays.stream(workerWrapper).collect(Collectors.toList());
        return Async.beginWork(timeout, executorService, workerWrappers);
    }

    public static boolean beginWork(long timeout, WorkerWrapper<?, ?> ... workerWrapper) throws ExecutionException, InterruptedException {
        return Async.beginWork(timeout, (ExecutorService)COMMON_POOL, workerWrapper);
    }

    public static void beginWorkAsync(long timeout, IGroupCallback groupCallback, WorkerWrapper<?, ?> ... workerWrapper) {
        Async.beginWorkAsync(timeout, COMMON_POOL, groupCallback, workerWrapper);
    }

    public static void beginWorkAsync(long timeout, ExecutorService executorService, IGroupCallback groupCallback, WorkerWrapper<?, ?> ... workerWrapper) {
        if (groupCallback == null) {
            groupCallback = new DefaultGroupCallback();
        }
        IGroupCallback finalGroupCallback = groupCallback;
        if (executorService != null) {
            executorService.submit(() -> {
                try {
                    boolean success = Async.beginWork(timeout, executorService, workerWrapper);
                    if (success) {
                        finalGroupCallback.success(Arrays.asList(workerWrapper));
                    } else {
                        finalGroupCallback.failure(Arrays.asList(workerWrapper), new TimeoutException());
                    }
                }
                catch (InterruptedException | ExecutionException e) {
                    finalGroupCallback.failure(Arrays.asList(workerWrapper), e);
                }
            });
        } else {
            COMMON_POOL.submit(() -> {
                try {
                    boolean success = Async.beginWork(timeout, (ExecutorService)COMMON_POOL, workerWrapper);
                    if (success) {
                        finalGroupCallback.success(Arrays.asList(workerWrapper));
                    } else {
                        finalGroupCallback.failure(Arrays.asList(workerWrapper), new TimeoutException());
                    }
                }
                catch (InterruptedException | ExecutionException e) {
                    finalGroupCallback.failure(Arrays.asList(workerWrapper), e);
                }
            });
        }
    }

    private static void totalWorkers(List<WorkerWrapper<?, ?>> workerWrappers, Set<WorkerWrapper<?, ?>> set) {
        set.addAll(workerWrappers);
        for (WorkerWrapper<?, ?> wrapper : workerWrappers) {
            if (wrapper.getNextWrappers() == null) continue;
            List<WorkerWrapper<?, ?>> wrappers = wrapper.getNextWrappers();
            Async.totalWorkers(wrappers, set);
        }
    }

    public static void shutDown() {
        Async.shutDown(executorService);
    }

    public static void shutDown(ExecutorService executorService) {
        if (executorService != null) {
            executorService.shutdown();
        } else {
            COMMON_POOL.shutdown();
        }
    }

    public static String getThreadCount() {
        return "activeCount=" + COMMON_POOL.getActiveCount() + "  completedCount " + COMMON_POOL.getCompletedTaskCount() + "  largestCount " + COMMON_POOL.getLargestPoolSize();
    }
}

