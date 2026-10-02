package com.kuma.cloud.lab.lock;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * volatile：可见性与原子性是两件事。写 volatile happens-before 后续对该字段的读，
 * 结合程序顺序和传递性，可以发布写之前的普通字段。
 * 它不是“让所有代码禁止重排”，也不宜简单解释为“刷新所有缓存”。
 * 单次读/写有相应保障，read-modify-write 组合却不是原子操作。
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se25/html/jls-17.html#jls-17.4.5">happens-before</a>
 */
public final class VolatileLesson {
    private int payload;
    private volatile boolean ready;
    private volatile int count;

    private VolatileLesson() {
    }

    public static void run() throws Exception {
        VolatileLesson lesson = new VolatileLesson();
        LockLearningDemo.parallel(() -> {
            lesson.payload = 42;
            lesson.ready = true; // 先写数据，再发布标记。
            return null;
        }, () -> {
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
            while (!lesson.ready) {
                if (Thread.currentThread().isInterrupted() || System.nanoTime() - deadline >= 0) {
                    throw new IllegalStateException("等待 volatile 发布超时或被中断");
                }
                Thread.onSpinWait(); // 只是自旋提示，同步保障来自 volatile。
            }
            LockLearningDemo.check(lesson.payload == 42, "读取发布标记后应看到之前的数据");
            return null;
        });
        CyclicBarrier bothRead = new CyclicBarrier(2);
        LockLearningDemo.parallel(() -> lesson.incrementWithOverlap(bothRead),
                () -> lesson.incrementWithOverlap(bothRead));
        LockLearningDemo.check(lesson.count == 1, "两个线程读到 0 后写入 1，应丢失一次更新");
        System.out.println("volatile：发布成功；两次非原子自增得到 1，而不是 2。");
    }

    /**
     * 展开 count++，屏障保证两线程在任何写入前都读到 0，稳定展示丢失更新。
     * 屏障也提供同步，但不能把两组读写变成互斥操作。普通 count++ 的竞态不保证每次出现。
     */
    private Void incrementWithOverlap(CyclicBarrier bothRead) throws Exception {
        int previous = count;
        bothRead.await(5, TimeUnit.SECONDS);
        count = previous + 1;
        return null;
    }
}
