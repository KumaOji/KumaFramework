package com.kuma.cloud.lab.lock;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * AQS（AbstractQueuedSynchronizer）是同步器框架，不是某一把锁，也不是无锁算法。
 * 教学模型：同步状态 state + 原子状态更新 + 等待队列 + 阻塞/唤醒。
 * 子类定义状态的含义与获取/释放规则，框架负责获取失败后的排队、中断和等待机制。
 * 独占模式一次只有一个持有者；共享模式能否继续由子类规则决定，并非所有线程总能通过。
 * 有 FIFO 等待队列不代表自动公平：新线程在入队前就可能获取成功。
 *
 * <p>本例是非公平、不可重入的教学 Mutex：0=空闲，1=占用。
 * ReentrantLock 则把 state 用作重入次数：持有者再次获取时递增，释放到 0 才真正解锁。
 * 不要在本例持锁期间再次 lock，那会等待自己释放。生产代码应优先用 JDK 锁。</p>
 */
public final class AqsLesson {
    private AqsLesson() {
    }

    public static void run() throws Exception {
        Mutex mutex = new Mutex();
        int[] count = {0};
        LockLearningDemo.parallel(() -> incrementMany(mutex, count), () -> incrementMany(mutex, count));
        LockLearningDemo.check(count[0] == 20_000, "AQS 独占锁应保护计数");

        mutex.lockInterruptibly();
        try {
            LockLearningDemo.check(!mutex.tryLock(), "教学 Mutex 不可重入");
            AtomicBoolean rejected = new AtomicBoolean();
            LockLearningDemo.parallel(() -> {
                try {
                    mutex.unlock();
                } catch (IllegalMonitorStateException expected) {
                    rejected.set(true);
                }
                return null;
            });
            LockLearningDemo.check(rejected.get(), "非持有者释放必须被拒绝");
        } finally {
            mutex.unlock();
        }
        System.out.println("AQS：自定义独占 Mutex 计数 20000，拒绝重入和非持有者释放。");
    }

    private static Void incrementMany(Mutex mutex, int[] count) throws InterruptedException {
        for (int i = 0; i < 10_000; i++) {
            mutex.lockInterruptibly();
            try {
                count[0]++;
            } finally {
                mutex.unlock();
            }
        }
        return null;
    }

    /** 只暴露实验需要的接口，不冒充具有全部 Lock 契约的生产锁。 */
    static final class Mutex {
        private final Sync sync = new Sync();

        void lockInterruptibly() throws InterruptedException {
            // 框架先尝试获取，失败则排队并可能 park；被唤醒后仍要重新检查获取条件。
            // unpark 是继续尝试的许可，不是把锁直接转交；线程也可能虚假唤醒。
            sync.acquireInterruptibly(1);
        }

        boolean tryLock() {
            return sync.tryAcquire(1);
        }

        void unlock() {
            // tryRelease 返回 true，表示完全释放，框架可以通知等待者继续尝试。
            sync.release(1);
        }

        private static final class Sync extends AbstractQueuedSynchronizer {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean tryAcquire(int ignored) {
                if (compareAndSetState(0, 1)) {
                    setExclusiveOwnerThread(Thread.currentThread());
                    return true;
                }
                return false;
            }

            @Override
            protected boolean tryRelease(int ignored) {
                if (getState() == 0 || getExclusiveOwnerThread() != Thread.currentThread()) {
                    throw new IllegalMonitorStateException("只有持有者可以释放 Mutex");
                }
                // 先清理 owner，再通过 volatile 状态写发布释放，避免覆盖新持有者的 owner。
                setExclusiveOwnerThread(null);
                setState(0);
                return true;
            }
        }
    }
}
