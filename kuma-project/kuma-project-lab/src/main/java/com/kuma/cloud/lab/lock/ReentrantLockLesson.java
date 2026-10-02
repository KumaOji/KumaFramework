package com.kuma.cloud.lab.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock：保护临界区，同时提供可重入、超时、中断获取和条件等待。
 * 可重入指同一线程再次获取同一把锁；每次成功获取都必须对应一次 unlock。
 * 解锁到后续成功获取同一把锁建立可见性，受保护字段不必再全部声明 volatile。
 * 默认非公平；公平锁在竞争时偏向等待更久的线程，但不保证操作系统调度公平。
 * 无参 tryLock 可以插队，即使是公平锁也如此，不能拿输出顺序证明公平性。
 * lock() 的获取不因中断退出；lockInterruptibly() 与带超时的 tryLock 可响应中断。
 * @see <a href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/locks/ReentrantLock.html">ReentrantLock 文档</a>
 */
public final class ReentrantLockLesson {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition available = lock.newCondition();
    private int count;
    private boolean ready;
    private int value;

    private ReentrantLockLesson() {
    }

    public static void run() throws Exception {
        ReentrantLockLesson lesson = new ReentrantLockLesson();
        lesson.lock.lock();
        try {
            lesson.lock.lock();
            try {
                LockLearningDemo.check(lesson.lock.getHoldCount() == 2, "重入后持有次数应为 2");
            } finally {
                lesson.lock.unlock();
            }
            // 主线程持续持锁，另一线程必定获取超时，无需 sleep 猜测执行时机。
            LockLearningDemo.parallel(() -> {
                boolean acquired = lesson.lock.tryLock(50, TimeUnit.MILLISECONDS);
                try {
                    LockLearningDemo.check(!acquired, "其他线程不能获取仍被主线程持有的锁");
                } finally {
                    if (acquired) {
                        lesson.lock.unlock(); // 只有获取成功才释放，不要对失败的 tryLock 解锁。
                    }
                }
                return null;
            });
        } finally {
            lesson.lock.unlock();
        }
        LockLearningDemo.check(!lesson.lock.isLocked(), "配对释放后应完全解锁");

        LockLearningDemo.parallel(() -> lesson.incrementMany(), () -> lesson.incrementMany());
        LockLearningDemo.check(lesson.count == 20_000, "同一把锁保护的自增应无丢失更新");
        LockLearningDemo.parallel(() -> {
            LockLearningDemo.check(lesson.take() == 42, "条件满足后应读取到数据");
            return null;
        }, () -> {
            lesson.publish(42);
            return null;
        });
        System.out.println("ReentrantLock：重入、超时、互斥计数和 Condition 数据交接通过。");
    }

    private Void incrementMany() throws InterruptedException {
        for (int i = 0; i < 10_000; i++) {
            lock.lockInterruptibly();
            try {
                count++;
            } finally {
                lock.unlock();
            }
        }
        return null;
    }

    /**
     * await 必须持有关联锁，等待时释放锁，返回/抛出中断异常前重新获取锁。
     * 用 while 重新判断业务条件，处理虚假唤醒和被其他线程先消费的情况。
     * 本例有超时上限，避免发布者出错时无限等待；它演示一次性交接，不是完整队列。
     */
    private int take() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            long remaining = TimeUnit.SECONDS.toNanos(5);
            while (!ready) {
                if (remaining <= 0) {
                    throw new IllegalStateException("等待条件超时");
                }
                remaining = available.awaitNanos(remaining);
            }
            return value;
        } finally {
            lock.unlock();
        }
    }

    private void publish(int newValue) {
        lock.lock();
        try {
            value = newValue;
            ready = true;
            available.signalAll(); // 只是通知；等待者须在当前线程解锁后重新竞争锁。
            // 若发布先发生，消费者看到 ready 就不等待：条件字段才是事实来源，通知不会存储。
        } finally {
            lock.unlock();
        }
    }
}
