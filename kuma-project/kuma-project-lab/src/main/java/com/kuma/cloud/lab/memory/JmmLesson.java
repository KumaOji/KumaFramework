package com.kuma.cloud.lab.memory;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Store buffering: T1: x=1; r1=y; T2: y=1; r2=x.
 * Plain fields permit (0,0); not observing it does not prove safety.
 * BOTH fields volatile exclude (0,0) through their synchronization order.
 * Barriers bracket iterations without ordering actors inside an iteration.
 */
public final class JmmLesson {
    private static final int ITERATIONS = 10_000;
    private int x, y, r1, r2, payload;
    private volatile int vx, vy;
    private boolean ready;
    private static final VarHandle READY;
    static {
        try {
            READY = MethodHandles.lookup().findVarHandle(JmmLesson.class, "ready", boolean.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    private JmmLesson() { }

    public static void run() throws Exception {
        new JmmLesson().storeBuffering(false);
        new JmmLesson().storeBuffering(true);
        new JmmLesson().releaseAcquire();
    }

    private void storeBuffering(boolean useVolatile) throws Exception {
        CyclicBarrier phase = new CyclicBarrier(3);
        var pool = Executors.newFixedThreadPool(2);
        long[] outcomes = new long[4];
        try {
            var first = pool.submit(() -> actor(phase, useVolatile, true));
            var second = pool.submit(() -> actor(phase, useVolatile, false));
            for (int i = 0; i < ITERATIONS; i++) {
                x = y = vx = vy = r1 = r2 = 0;
                phase.await(10, TimeUnit.SECONDS);
                phase.await(10, TimeUnit.SECONDS);
                MemoryLearningDemo.check((r1 == 0 || r1 == 1) && (r2 == 0 || r2 == 1), "非法读取结果");
                outcomes[r1 * 2 + r2]++;
            }
            first.get(10, TimeUnit.SECONDS);
            second.get(10, TimeUnit.SECONDS);
            if (useVolatile) MemoryLearningDemo.check(outcomes[0] == 0, "volatile 不允许 (0,0)");
            System.out.printf("Store buffering %s [(0,0),(0,1),(1,0),(1,1)]=%s%n",
                    useVolatile ? "volatile" : "plain", Arrays.toString(outcomes));
        } finally {
            pool.shutdownNow();
            MemoryLearningDemo.check(pool.awaitTermination(10, TimeUnit.SECONDS), "实验线程退出超时");
        }
    }

    private void actor(CyclicBarrier phase, boolean useVolatile, boolean first) {
        try {
            for (int i = 0; i < ITERATIONS; i++) {
                phase.await(10, TimeUnit.SECONDS);
                if (useVolatile) {
                    if (first) { vx = 1; r1 = vy; } else { vy = 1; r2 = vx; }
                } else {
                    if (first) { x = 1; r1 = y; } else { y = 1; r2 = x; }
                }
                phase.await(10, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Store buffering actor failed", e);
        }
    }

    /** Reader checks payload before Future.get: completion does not publish it to the reader. */
    private void releaseAcquire() throws Exception {
        var pool = Executors.newFixedThreadPool(2);
        try {
            var reader = pool.submit(() -> {
                long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
                while (!(boolean) READY.getAcquire(this)) {
                    if (Thread.currentThread().isInterrupted() || System.nanoTime() - deadline >= 0)
                        throw new IllegalStateException("acquire 等待超时");
                    Thread.onSpinWait();
                }
                MemoryLearningDemo.check(payload == 42, "release/acquire 发布失败");
            });
            var writer = pool.submit(() -> {
                payload = 42;
                READY.setRelease(this, true);
            });
            reader.get(15, TimeUnit.SECONDS);
            writer.get(15, TimeUnit.SECONDS);
            System.out.println("VarHandle release/acquire：普通字段 payload=42 发布通过。");
        } finally {
            pool.shutdownNow();
            MemoryLearningDemo.check(pool.awaitTermination(10, TimeUnit.SECONDS), "发布实验退出超时");
        }
    }
}
