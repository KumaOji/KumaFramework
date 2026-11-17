package com.kuma.cloud.lab.basics;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Future;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kuma.cloud.lab.basics.JavaBasicsLearningDemo.check;

final class VolatileLesson {
    private static final class State {
        int payload;
        volatile boolean ready;
        volatile int counter;
    }

    static void run() throws Exception {
        System.out.println("--- volatile：可见性、发布、复合操作非原子 ---");
        State state = new State();
        try (var pool = Executors.newFixedThreadPool(2)) {
            Future<Integer> reader = pool.submit(() -> {
                long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
                while (!state.ready) {
                    if (Thread.currentThread().isInterrupted() || System.nanoTime() > deadline)
                        throw new IllegalStateException("volatile publication timed out");
                    Thread.onSpinWait();
                }
                return state.payload;
            });
            state.payload = 42;
            state.ready = true;
            check("先写 payload 再 volatile 发布", reader.get(6, TimeUnit.SECONDS) == 42, "payload=42");
            CyclicBarrier bothRead = new CyclicBarrier(2);
            CyclicBarrier bothWrote = new CyclicBarrier(2);
            AtomicInteger atomic = new AtomicInteger();
            var task = (java.util.concurrent.Callable<Void>) () -> {
                for (int i = 0; i < 100; i++) {
                    // Explicitly split the read/modify/write performed by counter++.
                    int before = state.counter;
                    bothRead.await(5, TimeUnit.SECONDS);
                    state.counter = before + 1;
                    atomic.incrementAndGet();
                    bothWrote.await(5, TimeUnit.SECONDS);
                }
                return null;
            };
            Future<Void> first = pool.submit(task);
            Future<Void> second = pool.submit(task);
            first.get(15, TimeUnit.SECONDS);
            second.get(15, TimeUnit.SECONDS);
            check("强制相同读取值：volatile 丢失更新", state.counter == 100, state.counter);
            check("AtomicInteger 对照", atomic.get() == 200, atomic.get());
        }
        System.out.println("屏障用于固定交错；此实验不统计自然 counter++ 的丢失频率。普通字段自旋也不保证复现不可见。");
    }
}
