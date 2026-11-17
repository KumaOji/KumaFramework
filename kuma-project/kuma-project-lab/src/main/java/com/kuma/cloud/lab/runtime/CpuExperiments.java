package com.kuma.cloud.lab.runtime;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.kuma.cloud.lab.runtime.RuntimeLearningDemo.*;

final class CpuExperiments {
    private static volatile long blackhole;

    static void time() throws Exception {
        System.out.println("--- CPU：wall time 与平台线程 CPU time ---");
        ThreadMXBean threads = ManagementFactory.getThreadMXBean();
        System.out.println("availableProcessors=" + Runtime.getRuntime().availableProcessors());
        boolean supported = threads.isCurrentThreadCpuTimeSupported();
        boolean enabledBefore = supported && threads.isThreadCpuTimeEnabled();
        try {
            if (supported && !enabledBefore) threads.setThreadCpuTimeEnabled(true);
            long cpuStart = supported ? threads.getCurrentThreadCpuTime() : -1;
            long wallStart = System.nanoTime();
            long result = sum(0, 2_000_000);
            blackhole = result;
            long busyWall = System.nanoTime() - wallStart;
            long busyCpu = supported ? threads.getCurrentThreadCpuTime() - cpuStart : -1;
            cpuStart = supported ? threads.getCurrentThreadCpuTime() : -1;
            wallStart = System.nanoTime();
            boolean released = new CountDownLatch(1).await(60, TimeUnit.MILLISECONDS);
            long waitWall = System.nanoTime() - wallStart;
            long waitCpu = supported ? threads.getCurrentThreadCpuTime() - cpuStart : -1;
            // Checksum independently calculated for the fixed input range, outside the timed work.
            check("CPU 工作结果与有限等待完成", result == 62_000_521_755_456L && !released, result);
            System.out.println("busy wallNs=" + busyWall + ", cpuNs=" + busyCpu);
            System.out.println("wait wallNs=" + waitWall + ", cpuNs=" + waitCpu + ", supported=" + supported);
            System.out.println("processCpu=" + ProcessHandle.current().info().totalCpuDuration());
        } finally { if (supported && !enabledBefore) threads.setThreadCpuTimeEnabled(false); }
        System.out.println("CPU计时可能不受支持（-1），精度和调度影响数值，不断言固定比例。ThreadMXBean此处监控平台线程。");
    }

    static void parallel() throws Exception {
        System.out.println("--- CPU：有界并行分片与结果合并 ---");
        long start = System.nanoTime();
        long serial = sum(0, 2_000_000);
        long serialNanos = System.nanoTime() - start;
        workers(2, pool -> {
            long parallelStart = System.nanoTime();
            var first = pool.submit(() -> sum(0, 1_000_000));
            var second = pool.submit(() -> sum(1_000_000, 2_000_000));
            long parallel = get(first) + get(second);
            check("两个分片无共享计数、结果等于串行", serial == parallel, parallel);
            blackhole = parallel;
            System.out.println("serialNs=" + serialNanos + ", parallelWallNs=" + (System.nanoTime() - parallelStart));
        });
        System.out.println("线程数固定2，不随机器核数无限创建线程；耗时包含启动/调度/JIT，不保证并行更快，不是基准测试。");
    }

    static void states() throws Exception {
        System.out.println("--- CPU/调度：BLOCKED 与 TIMED_WAITING ---");
        Object monitor = new Object();
        AtomicReference<Thread> blockedThread = new AtomicReference<>(), waitingThread = new AtomicReference<>();
        CountDownLatch attempting = new CountDownLatch(1), waiting = new CountDownLatch(1), release = new CountDownLatch(1);
        workers(2, pool -> {
            java.util.concurrent.Future<Boolean> blocked;
            synchronized (monitor) {
                blocked = pool.submit(() -> {
                    blockedThread.set(Thread.currentThread()); attempting.countDown();
                    synchronized (monitor) { return true; }
                });
                await(attempting);
                until(() -> blockedThread.get().getState() == Thread.State.BLOCKED);
                var info = ManagementFactory.getThreadMXBean().getThreadInfo(blockedThread.get().threadId());
                check("monitor 竞争记录持有者", info != null && info.getThreadState() == Thread.State.BLOCKED
                        && info.getLockOwnerId() == Thread.currentThread().threadId(), info == null ? "missing" : info.getLockName());
            }
            check("释放 monitor 后工作继续", get(blocked), true);
            var timed = pool.submit(() -> {
                waitingThread.set(Thread.currentThread()); waiting.countDown();
                return release.await(5, TimeUnit.SECONDS);
            });
            try {
                await(waiting);
                until(() -> waitingThread.get().getState() == Thread.State.TIMED_WAITING);
                check("Latch 有限等待状态", waitingThread.get().getState() == Thread.State.TIMED_WAITING, waitingThread.get().getState());
            } finally { release.countDown(); }
            check("通知后任务继续", get(timed), true);
        });
        System.out.println("BLOCKED是等待monitor，TIMED_WAITING是带超时等待；RUNNABLE不能直接解释为占满CPU。");
    }

    private static long sum(int from, int to) {
        long result = 0;
        for (long i = from; i < to; i++) result += (i * 31) ^ (i >>> 3);
        return result;
    }
}
