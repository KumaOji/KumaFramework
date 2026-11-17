package com.kuma.cloud.lab.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

import static com.kuma.cloud.lab.concurrent.ConcurrencyLearningDemo.*;

final class LockExperiments {
    static void monitor() throws Exception {
        System.out.println("--- synchronized：互斥、可重入、异常释放 ---");
        Object monitor = new Object();
        int[] count = {0};
        workers(2, pool -> {
            var task = (java.util.concurrent.Callable<Void>) () -> {
                for (int i = 0; i < 1000; i++) synchronized (monitor) {
                    synchronized (monitor) { count[0]++; }
                }
                return null;
            };
            var first = submit(pool, task);
            var second = submit(pool, task);
            get(first); get(second);
            check("同一 monitor 保护计数并允许重入", count[0] == 2000, count[0]);
            try { synchronized (monitor) { throw new IllegalArgumentException("expected"); } }
            catch (IllegalArgumentException expected) { /* Verify acquisition from another thread below. */ }
            check("异常退出后另一线程可获取 monitor", get(submit(pool, () -> {
                synchronized (monitor) { return Thread.holdsLock(monitor); }
            })), true);
        });
        System.out.println("互斥需要共享同一个 monitor；实例 synchronized 锁 this，static synchronized 锁 Class。");
    }

    static void reentrant() throws Exception {
        System.out.println("--- ReentrantLock：重入、公平配置、tryLock 超时、可中断获取、Condition ---");
        ReentrantLock lock = new ReentrantLock(true);
        workers(2, pool -> {
            lock.lock(); lock.lock();
            try {
                check("重入计数与公平配置", lock.getHoldCount() == 2 && lock.isFair(), lock.getHoldCount());
                check("其他线程 timed tryLock 获取失败", !get(submit(pool, () -> {
                    boolean acquired = lock.tryLock(50, TimeUnit.MILLISECONDS);
                    if (acquired) lock.unlock();
                    return acquired;
                })), false);
                CountDownLatch attempted = new CountDownLatch(1), interrupted = new CountDownLatch(1);
                var waiter = submit(pool, () -> {
                    attempted.countDown();
                    try {
                        lock.lockInterruptibly();
                        try { throw new IllegalStateException("Main still holds lock"); }
                        finally { lock.unlock(); }
                    } catch (InterruptedException expected) { interrupted.countDown(); }
                    return null;
                });
                await(attempted);
                waiter.cancel(true);
                await(interrupted);
                check("lockInterruptibly 响应中断", interrupted.getCount() == 0, "interrupted");
            } finally { lock.unlock(); lock.unlock(); }

            var condition = lock.newCondition();
            boolean[] ready = {false};
            CountDownLatch entered = new CountDownLatch(1);
            var consumer = submit(pool, () -> {
                lock.lockInterruptibly();
                try {
                    entered.countDown();
                    long remaining = TimeUnit.SECONDS.toNanos(5);
                    while (!ready[0]) {
                        if (remaining <= 0) throw new IllegalStateException("Condition timed out");
                        remaining = condition.awaitNanos(remaining);
                    }
                    return ready[0];
                } finally { lock.unlock(); }
            });
            await(entered);
            lock.lock();
            try { ready[0] = true; condition.signalAll(); }
            finally { lock.unlock(); }
            check("Condition 释放锁等待、醒来重新持锁检查条件", get(consumer), true);
        });
        System.out.println("公平配置不等于固定线程执行顺序；无参 tryLock 可以插队，不把调度顺序作为断言。");
    }

    static void readWrite() throws Exception {
        System.out.println("--- ReentrantReadWriteLock：共享读、排他写、锁降级 ---");
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        workers(1, pool -> {
            lock.readLock().lock();
            try {
                check("另一个读者可同时获取", get(submit(pool, () -> {
                    boolean acquired = lock.readLock().tryLock(1, TimeUnit.SECONDS);
                    if (acquired) lock.readLock().unlock();
                    return acquired;
                })), true);
                check("存在读者时写者获取失败", !get(submit(pool, () -> {
                    boolean acquired = lock.writeLock().tryLock(50, TimeUnit.MILLISECONDS);
                    if (acquired) lock.writeLock().unlock();
                    return acquired;
                })), false);
            } finally { lock.readLock().unlock(); }
            lock.writeLock().lock();
            try { lock.readLock().lock(); }
            finally { lock.writeLock().unlock(); }
            try {
                check("写锁降级后仍保留读锁", lock.getReadHoldCount() == 1 && !lock.isWriteLocked(), "readHold=1");
            } finally { lock.readLock().unlock(); }
        });
        System.out.println("读锁不能直接阻塞升级为写锁；需要释放读锁并在获得写锁后重新验证条件。");
    }

    static void stamped() throws Exception {
        System.out.println("--- StampedLock：乐观读校验失败与读锁回退 ---");
        StampedLock lock = new StampedLock();
        int[] data = {1};
        long stamp = lock.tryOptimisticRead();
        int speculative = data[0];
        workers(1, pool -> get(submit(pool, () -> {
            long write = lock.writeLock();
            try { data[0] = 2; } finally { lock.unlockWrite(write); }
            return null;
        })));
        boolean valid = lock.validate(stamp);
        check("写入使旧乐观读戳失效", !valid && speculative == 1, "valid=" + valid + ", speculative=" + speculative);
        long read = lock.readLock();
        try { check("回退读锁获得一致值", data[0] == 2, data[0]); }
        finally { lock.unlockRead(read); }
        System.out.println("乐观读不是持有读锁，校验前的数据不可直接交给业务；StampedLock 不支持重入。");
    }

    static void semaphore() throws Exception {
        System.out.println("--- Semaphore：共享许可与资源上限 ---");
        Semaphore semaphore = new Semaphore(2);
        AtomicInteger active = new AtomicInteger();
        CountDownLatch bothInside = new CountDownLatch(2), release = new CountDownLatch(1);
        workers(3, pool -> {
            var holder = (java.util.concurrent.Callable<Void>) () -> {
                if (!semaphore.tryAcquire(5, TimeUnit.SECONDS)) throw new IllegalStateException("Acquire timed out");
                try { active.incrementAndGet(); bothInside.countDown(); await(release); }
                finally { active.decrementAndGet(); semaphore.release(); }
                return null;
            };
            var first = submit(pool, holder); var second = submit(pool, holder);
            try {
                await(bothInside);
                check("两个许可允许两个任务同时进入", active.get() == 2, active.get());
                check("第三个任务无法获取已耗尽许可", !get(submit(pool, () -> {
                    boolean acquired = semaphore.tryAcquire(50, TimeUnit.MILLISECONDS);
                    if (acquired) semaphore.release();
                    return acquired;
                })), false);
            } finally { release.countDown(); }
            get(first); get(second);
            check("finally 归还许可", semaphore.availablePermits() == 2 && active.get() == 0, semaphore.availablePermits());
        });
        System.out.println("Semaphore 是许可计数器，不记录持有线程；只能在获取成功后 release，避免许可膨胀。");
    }

    static void waitNotify() throws Exception {
        System.out.println("--- Object.wait / notifyAll：条件循环与 monitor 释放 ---");
        Object monitor = new Object();
        int[] value = {0};
        CountDownLatch entered = new CountDownLatch(1);
        workers(1, pool -> {
            var reader = submit(pool, () -> {
                synchronized (monitor) {
                    entered.countDown();
                    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
                    while (value[0] == 0) {
                        long remaining = deadline - System.nanoTime();
                        if (remaining <= 0) throw new IllegalStateException("wait timed out");
                        TimeUnit.NANOSECONDS.timedWait(monitor, remaining);
                    }
                    return value[0];
                }
            });
            await(entered);
            synchronized (monitor) { value[0] = 42; monitor.notifyAll(); }
            check("wait 释放 monitor 后写者可更新并通知", get(reader) == 42, 42);
        });
        try { monitor.notifyAll(); throw new IllegalStateException("Expected monitor ownership error"); }
        catch (IllegalMonitorStateException expected) { System.out.println("未持有 monitor 调用 notifyAll -> IllegalMonitorStateException (expected)"); }
    }

    static void deadlock() throws Exception {
        System.out.println("--- 交叉持锁：有限等待识别死锁条件，并中断恢复 ---");
        ReentrantLock left = new ReentrantLock(), right = new ReentrantLock();
        CountDownLatch bothHeld = new CountDownLatch(2), trying = new CountDownLatch(2);
        AtomicInteger interrupted = new AtomicInteger();
        workers(2, pool -> {
            var first = submit(pool, () -> { crossLock(left, right, bothHeld, trying, interrupted); return null; });
            var second = submit(pool, () -> { crossLock(right, left, bothHeld, trying, interrupted); return null; });
            await(trying);
            // Each worker still owns its first lock, so neither second acquisition can succeed.
            check("交叉持锁双方未完成", !first.isDone() && !second.isDone(), "two unfinished tasks");
            first.cancel(true); second.cancel(true);
        });
        check("中断解除等待并释放所有锁", interrupted.get() >= 1 && !left.isLocked() && !right.isLocked(), interrupted.get());
        System.out.println("这是可中断 ReentrantLock 的循环等待恢复实验；不创建无法回收的 synchronized 死锁。"
                + "统一锁顺序可避免本例循环等待，生产诊断还可使用线程转储。");
    }

    private static void crossLock(ReentrantLock first, ReentrantLock second, CountDownLatch bothHeld,
                                  CountDownLatch trying, AtomicInteger interrupted) throws Exception {
        first.lockInterruptibly();
        try {
            bothHeld.countDown(); await(bothHeld); trying.countDown();
            try {
                second.lockInterruptibly();
                try { /* Other worker may progress after its peer has been interrupted. */ }
                finally { second.unlock(); }
            } catch (InterruptedException expected) { interrupted.incrementAndGet(); }
        } finally { first.unlock(); }
    }
}
