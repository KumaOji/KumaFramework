package com.kuma.cloud.lab.runtime;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kuma.cloud.lab.runtime.RuntimeLearningDemo.*;

final class PoolExperiments {
    static void growth() throws Exception {
        System.out.println("--- 线程池：core -> queue -> max -> reject ---");
        var pool = new ThreadPoolExecutor(1, 2, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1));
        CountDownLatch firstEntered = new CountDownLatch(1), extraEntered = new CountDownLatch(1), release = new CountDownLatch(1);
        try {
            var first = pool.submit(() -> { firstEntered.countDown(); await(release); return 1; });
            await(firstEntered);
            var queued = pool.submit(() -> 2);
            check("达到core后先排队", pool.getPoolSize() == 1 && pool.getQueue().size() == 1, "pool=1, queued=1");
            var extra = pool.submit(() -> { extraEntered.countDown(); await(release); return 3; });
            await(extraEntered);
            check("队列满后扩到max", pool.getPoolSize() == 2 && pool.getQueue().size() == 1, "pool=2, queued=1");
            try { pool.submit(() -> 4); throw new IllegalStateException("Expected rejection"); }
            catch (RejectedExecutionException expected) { System.out.println("达到max且队列已满 -> RejectedExecutionException (expected)"); }
            release.countDown();
            check("接受的任务全部完成", get(first) + get(queued) + get(extra) == 6, "1+2+3=6");
        } finally { release.countDown(); stop(pool); }
    }

    static void shutdown() throws Exception {
        System.out.println("--- 线程池：shutdown 与 shutdownNow/排队 Future ---");
        var graceful = Executors.newSingleThreadExecutor();
        try {
            var accepted = graceful.submit(() -> 42);
            graceful.shutdown();
            try { graceful.submit(() -> 0); throw new IllegalStateException("Expected shutdown rejection"); }
            catch (RejectedExecutionException expected) { System.out.println("shutdown 后拒绝新任务 (expected)"); }
            check("shutdown 允许已接受任务完成", get(accepted) == 42, 42);
        } finally { stop(graceful); }
        var abrupt = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1));
        CountDownLatch entered = new CountDownLatch(1), interrupted = new CountDownLatch(1);
        try {
            var active = abrupt.submit(() -> {
                entered.countDown();
                try { new CountDownLatch(1).await(5, TimeUnit.SECONDS); }
                catch (InterruptedException expected) { interrupted.countDown(); }
                return 1;
            });
            await(entered);
            var queued = abrupt.submit(() -> 2);
            List<Runnable> drained = abrupt.shutdownNow();
            check("shutdownNow 返回未开始任务，不自动取消其Future", drained.size() == 1 && drained.getFirst() == queued
                    && !queued.isDone(), "drained=1; queued.isDone=" + queued.isDone());
            for (Runnable task : drained) if (task instanceof Future<?> future) future.cancel(false);
            await(interrupted);
            check("运行中任务协作响应中断，排队任务显式取消", get(active) == 1 && queued.isCancelled(), "active finished, queued cancelled");
        } finally { stop(abrupt); }
    }

    static void virtual() throws Exception {
        System.out.println("--- 虚拟线程：任务数量与下游并发容量分离 ---");
        var tasks = Executors.newVirtualThreadPerTaskExecutor();
        Semaphore resources = new Semaphore(3);
        CountDownLatch threeInside = new CountDownLatch(3), release = new CountDownLatch(1);
        AtomicInteger active = new AtomicInteger(), peak = new AtomicInteger();
        List<Future<Boolean>> results = new java.util.ArrayList<>();
        try {
            for (int i = 0; i < 32; i++) results.add(tasks.submit(() -> {
                if (!resources.tryAcquire(5, TimeUnit.SECONDS)) throw new IllegalStateException("Resource acquisition timed out");
                try {
                    int now = active.incrementAndGet(); peak.accumulateAndGet(now, Math::max);
                    threeInside.countDown(); await(release);
                    return Thread.currentThread().isVirtual();
                } finally { active.decrementAndGet(); resources.release(); }
            }));
            await(threeInside);
            check("32个任务仍受3个下游许可限制", active.get() == 3, active.get());
            release.countDown();
            boolean allVirtual = true;
            for (Future<Boolean> result : results) allVirtual &= get(result);
            check("32个任务均运行于虚拟线程", allVirtual, allVirtual);
            check("下游峰值为3且最终全部归还", peak.get() == 3 && active.get() == 0 && resources.availablePermits() == 3, peak.get());
        } finally { release.countDown(); stop(tasks); }
        System.out.println("这是每任务一个虚拟线程的执行器；虚拟线程不会自动增加数据库连接数或CPU容量。");
    }
}
