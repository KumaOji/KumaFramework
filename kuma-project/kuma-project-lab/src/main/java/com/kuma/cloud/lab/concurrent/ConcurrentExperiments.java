package com.kuma.cloud.lab.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

import static com.kuma.cloud.lab.concurrent.ConcurrencyLearningDemo.*;

final class ConcurrentExperiments {
    static void atomic() throws Exception {
        System.out.println("--- 原子更新：固定丢失更新、CAS、ABA 与 ConcurrentHashMap.compute ---");
        int[] unsafe = {0};
        AtomicInteger safe = new AtomicInteger();
        ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<>();
        CyclicBarrier read = new CyclicBarrier(2), wrote = new CyclicBarrier(2);
        workers(2, pool -> {
            var task = (java.util.concurrent.Callable<Void>) () -> {
                for (int i = 0; i < 100; i++) {
                    int snapshot = unsafe[0];
                    read.await(5, TimeUnit.SECONDS);
                    unsafe[0] = snapshot + 1;
                    safe.incrementAndGet();
                    counts.compute("key", (key, previous) -> previous == null ? 1 : previous + 1);
                    wrote.await(5, TimeUnit.SECONDS);
                }
                return null;
            };
            var first = submit(pool, task); var second = submit(pool, task);
            get(first); get(second);
        });
        check("固定交错丢失更新", unsafe[0] == 100, unsafe[0]);
        check("AtomicInteger 与单键 compute 原子更新", safe.get() == 200 && counts.get("key") == 200,
                safe.get() + "/" + counts.get("key"));
        Object a = new Object(), b = new Object(), replacement = new Object();
        AtomicReference<Object> reference = new AtomicReference<>(a);
        Object observed = reference.get();
        check("普通 CAS 未识别 A-B-A 历史", reference.compareAndSet(a, b)
                && reference.compareAndSet(b, a) && reference.compareAndSet(observed, replacement), true);
        AtomicStampedReference<Object> stamped = new AtomicStampedReference<>(a, 0);
        int[] oldStamp = new int[1];
        Object oldReference = stamped.get(oldStamp);
        check("版本戳更新 A-B-A", stamped.compareAndSet(a, b, 0, 1) && stamped.compareAndSet(b, a, 1, 2), stamped.getStamp());
        check("带旧 stamp 的 CAS 被拒绝", !stamped.compareAndSet(oldReference, replacement, oldStamp[0], oldStamp[0] + 1), stamped.getStamp());
        System.out.println("ABA 示例用顺序操作模拟明确的交错历史，不依赖线程调度。屏障只用于固定计数实验的读写时序。"
                + "ConcurrentHashMap 只保证所用原子API的范围，不保证跨多个键的整体事务。");
    }

    static void coordination() throws Exception {
        System.out.println("--- 线程协作：CountDownLatch / CyclicBarrier / Phaser ---");
        CountDownLatch ready = new CountDownLatch(2), start = new CountDownLatch(1), done = new CountDownLatch(2);
        AtomicInteger barrierRounds = new AtomicInteger(), work = new AtomicInteger();
        CyclicBarrier barrier = new CyclicBarrier(2, barrierRounds::incrementAndGet);
        Phaser phaser = new Phaser(2);
        workers(2, pool -> {
            var task = (java.util.concurrent.Callable<Void>) () -> {
                ready.countDown(); await(start);
                try {
                    for (int i = 0; i < 2; i++) {
                        work.incrementAndGet();
                        barrier.await(5, TimeUnit.SECONDS);
                    }
                    int phase = phaser.arrive();
                    phaser.awaitAdvanceInterruptibly(phase, 5, TimeUnit.SECONDS);
                    phaser.arriveAndDeregister();
                    return null;
                } finally { done.countDown(); }
            };
            var first = submit(pool, task); var second = submit(pool, task);
            try { await(ready); }
            finally { start.countDown(); }
            await(done); get(first); get(second);
        });
        check("Latch 一次性开始/完成通知", done.getCount() == 0 && work.get() == 4, work.get());
        check("Barrier 可复用两个轮次", barrierRounds.get() == 2, barrierRounds.get());
        check("Phaser 动态注销后终止", phaser.getRegisteredParties() == 0 && phaser.isTerminated(), phaser.getRegisteredParties());
    }

    static void queue() throws Exception {
        System.out.println("--- ArrayBlockingQueue：容量、背压、生产消费与终止标记 ---");
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(2);
        check("有界队列满时 offer 拒绝", queue.offer(1) && queue.offer(2) && !queue.offer(3), queue.size());
        check("FIFO 顺序", queue.poll() == 1 && queue.poll() == 2, "1,2");
        workers(2, pool -> {
            var producer = submit(pool, () -> {
                for (int value : new int[] {1, 2, 3, 4, 5, -1}) {
                    if (!queue.offer(value, 5, TimeUnit.SECONDS)) throw new IllegalStateException("Producer timed out");
                }
                return null;
            });
            var consumer = submit(pool, () -> {
                List<Integer> received = new ArrayList<>();
                while (true) {
                    Integer value = queue.poll(5, TimeUnit.SECONDS);
                    if (value == null) throw new IllegalStateException("Consumer timed out");
                    if (value == -1) return received;
                    received.add(value);
                }
            });
            get(producer);
            List<Integer> received = get(consumer);
            check("真实并发生产消费按序完成", received.equals(List.of(1, 2, 3, 4, 5)) && queue.isEmpty(), received);
        });
        System.out.println("本例一个消费者对应一个终止标记；多个消费者需要相应数量标记或独立生命周期协议。");
    }

    static void future() throws Exception {
        System.out.println("--- Future / CompletableFuture：组合、异常、取消与协作中断 ---");
        workers(2, pool -> {
            var left = CompletableFuture.supplyAsync(() -> 20, pool);
            var right = CompletableFuture.supplyAsync(() -> 22, pool);
            check("thenCombine 组合结果", get(left.thenCombine(right, Integer::sum)) == 42, 42);
            CompletableFuture<Integer> failed = CompletableFuture.supplyAsync(() -> {
                throw new IllegalArgumentException("expected");
            }, pool);
            check("exceptionally 显式降级", get(failed.exceptionally(error -> -1)) == -1, -1);
            try { failed.join(); throw new IllegalStateException("Expected completion error"); }
            catch (CompletionException expected) {
                check("异步异常保留 cause", expected.getCause() instanceof IllegalArgumentException, expected.getCause().getClass().getSimpleName());
            }
            CountDownLatch started = new CountDownLatch(1), interrupted = new CountDownLatch(1);
            var cancellable = submit(pool, () -> {
                started.countDown();
                try { new CountDownLatch(1).await(5, TimeUnit.SECONDS); }
                catch (InterruptedException expected) { interrupted.countDown(); }
                return null;
            });
            await(started);
            check("Future.cancel(true) 请求中断", cancellable.cancel(true), true);
            await(interrupted);
            try { get(cancellable); throw new IllegalStateException("Expected cancellation"); }
            catch (CancellationException expected) { System.out.println("已取消 Future.get -> CancellationException (expected)"); }
            check("底层任务实际响应中断", interrupted.getCount() == 0, true);
        });
        System.out.println("本例取消的是 ExecutorService 返回的 Future；CompletableFuture.cancel(true) 不保证中断底层计算。"
                + "取消是请求，任务必须协作响应。");
    }

    static void threadLocal() throws Exception {
        System.out.println("--- ThreadLocal：线程复用残留、任务隔离与 finally.remove ---");
        ThreadLocal<String> context = new ThreadLocal<>();
        workers(1, pool -> {
            context.set("caller");
            try {
                check("普通 ThreadLocal 不自动传播到工作线程", get(submit(pool, () -> context.get())) == null, "worker=null");
                get(submit(pool, () -> { context.set("task-one"); return null; }));
                check("单工作线程复用导致残留", "task-one".equals(get(submit(pool, () -> context.get()))), "task-one");
                get(submit(pool, () -> {
                    try { context.set("task-two"); return context.get(); }
                    finally { context.remove(); }
                }));
                check("finally.remove 后下一任务无残留", get(submit(pool, () -> context.get())) == null, "worker=null");
                check("主线程上下文独立", "caller".equals(context.get()), context.get());
            } finally { context.remove(); }
        });
    }
}
