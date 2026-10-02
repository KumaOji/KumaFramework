package com.kuma.cloud.lab.lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * CountDownLatch：一次性门闩。构造参数是需要完成的事件数，不一定是线程数。
 * countDown 不等待，只把计数减一；await 等待归零，多个等待者可在归零后继续。
 * 归零之后不能重置，额外 countDown 无效；循环阶段协调可以了解 CyclicBarrier 或 Phaser。
 *
 * <p>JDK 的 Sync 使用 AQS 共享模式：state 是剩余计数，countDown 通过 CAS 减计数；
 * await 在 state 为 0 时成功，否则进入共享等待。最后一次减到 0 触发等待者放行。
 * countDown 之前的操作 happens-before 对应 await 成功返回之后的操作。
 * await 超时返回 false 时不能把任务结果视为全部就绪。</p>
 * @see <a href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/CountDownLatch.html">CountDownLatch 文档</a>
 */
public final class CountDownLatchLesson {
    private CountDownLatchLesson() {
    }

    public static void run() throws Exception {
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(2);
        int[] results = new int[2];
        // 至少有容纳全部等待任务和协调者的执行资源，否则线程池可能发生饥饿死锁。
        LockLearningDemo.parallel(() -> compute(start, done, results, 0, 20),
                () -> compute(start, done, results, 1, 22), () -> {
                    LockLearningDemo.check(!done.await(0, TimeUnit.SECONDS), "开始前任务不应完成");
                    start.countDown();
                    LockLearningDemo.check(done.await(5, TimeUnit.SECONDS), "等待任务完成超时");
                    // 在这里检查，而非 Future.get 之后检查，明确展示 done.await 的结果发布作用。
                    // 每个任务写不同数组位置；Latch 只协调完成，不提供对同一位置写入的互斥。
                    LockLearningDemo.check(results[0] + results[1] == 42, "归零后应看到普通数组结果");
                    done.countDown();
                    LockLearningDemo.check(done.getCount() == 0, "归零后不能继续减成负数");
                    LockLearningDemo.check(done.await(0, TimeUnit.SECONDS), "门闩打开后再次 await 立即成功");
                    return null;
                });
        System.out.println("CountDownLatch：启动门闩、完成等待、普通数组结果发布和一次性语义通过。");
    }

    private static Void compute(CountDownLatch start, CountDownLatch done,
                                int[] results, int index, int value) throws InterruptedException {
        try {
            if (!start.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("等待启动超时");
            }
            results[index] = value;
        } finally {
            done.countDown(); // 即使失败也报告任务结束，避免协调者永远等待。
            // 结束不等于成功：Latch 不携带异常，入口通过 Future.get 另外传播任务失败。
        }
        return null;
    }
}
