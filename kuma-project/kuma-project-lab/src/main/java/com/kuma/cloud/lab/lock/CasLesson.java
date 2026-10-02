package com.kuma.cloud.lab.lock;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * CAS：仅当当前值等于预期值时，原子更新并返回 true。比较和更新是一个原子操作，
 * 不是先执行 Java if 再普通赋值。竞争失败后要重新读取、计算，不能一直用旧预期值重试。
 * compareAndSet 有 volatile 读写内存效果；plain、opaque、acquire/release 等模式的
 * 语义不同，不能认为原子类的所有方法都有相同内存效果。
 * @see <a href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/atomic/package-summary.html">原子类文档</a>
 */
public final class CasLesson {
    private CasLesson() {
    }

    public static void run() throws Exception {
        AtomicInteger count = new AtomicInteger();
        LockLearningDemo.parallel(() -> incrementMany(count), () -> incrementMany(count));
        LockLearningDemo.check(count.get() == 20_000, "CAS 计数不能丢失更新");

        // 固定重放 ABA 历史：观察者记住 A，其他修改者 A -> B -> A，不依赖调度碰运气。
        Object a = new Object();
        Object b = new Object();
        Object c = new Object();
        AtomicReference<Object> reference = new AtomicReference<>(a);
        Object observed = reference.get();
        reference.set(b);
        reference.set(a);
        LockLearningDemo.check(reference.compareAndSet(observed, c), "普通引用无法识别 ABA");

        AtomicStampedReference<Object> stamped = new AtomicStampedReference<>(a, 0);
        int[] stamp = new int[1];
        Object observedValue = stamped.get(stamp); // 一次读取引用和版本，避免分开读取发生混配。
        stamped.set(b, 1);
        stamped.set(a, 2);
        LockLearningDemo.check(!stamped.compareAndSet(observedValue, c, stamp[0], 3), "旧版本应失败");
        // 所有修改者必须按协议更新版本；版本回绕/复用仍需业务设计考虑。
        // AtomicReference 比较引用身份，不调用 equals；ABA 是否有害取决于算法的不变量。
        System.out.println("CAS：计数 20000；普通引用接受 ABA，带版本号的引用拒绝旧版本。");
    }

    private static Void incrementMany(AtomicInteger count) throws InterruptedException {
        for (int i = 0; i < 10_000; i++) {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException("取消 CAS 实验");
                }
                int previous = count.get();
                if (count.compareAndSet(previous, previous + 1)) {
                    break;
                }
                // 重试消耗 CPU，单个线程可能长期竞争失败；不要在重试区域扣款、发消息。
                Thread.onSpinWait();
            }
        }
        // 实际独立计数通常直接用 incrementAndGet。高频统计可了解 LongAdder，
        // 但它的 sum 不是并发更新下的原子快照。
        return null;
    }
}
