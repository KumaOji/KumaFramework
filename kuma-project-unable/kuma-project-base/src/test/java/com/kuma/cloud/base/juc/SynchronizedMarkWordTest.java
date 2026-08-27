package com.kuma.cloud.base.juc;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;

/**
 * synchronized 对 Mark Word 影响的可视化测试
 *
 * <p>JDK 18+ 已移除偏向锁（JEP 374），64位 Mark Word 布局如下：
 *
 * <pre>
 * ┌──────────────────────────────────────────────────────────────────────┐
 * │                      Mark Word (64 bit)                              │
 * ├─────────────────────────────────────────────────────────┬────────────┤
 * │  状态         │  内容                                    │  低2位标志 │
 * ├───────────────┼──────────────────────────────────────────┼────────────┤
 * │  无锁 unlocked│  identity_hashcode(31) | unused | age(4) │  ...0  01  │
 * │  轻量锁       │  ptr → 栈帧中 Lock Record(62bit)          │  ...   00  │
 * │  重量锁       │  ptr → ObjectMonitor 堆对象(62bit)        │  ...   10  │
 * │  GC 标记      │  forwarding pointer(62bit)               │  ...   11  │
 * └───────────────┴──────────────────────────────────────────┴────────────┘
 * </pre>
 *
 * <p>运行时请添加 JVM 参数：-XX:+UnlockDiagnosticVMOptions -XX:+PrintCompilation
 * 以及 --add-opens java.base/java.lang=ALL-UNNAMED（JOL 需要反射访问）
 */
class SynchronizedMarkWordTest {

    // ------------------------------------------------------------------ //
    //  辅助：打印带边框的 Mark Word 信息                                    //
    // ------------------------------------------------------------------ //

    private static void printHeader(String title) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.printf( "║  %-52s║%n", title);
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }

    private static void printMarkWord(Object obj) {
        System.out.println(ClassLayout.parseInstance(obj).toPrintable());
    }

    private static String lockState(Object obj) {
        // 读取 Mark Word 低2位来判断锁状态
        // JOL 解析后的第一行包含 mark word 原始值
        String layout = ClassLayout.parseInstance(obj).toPrintable();
        // 取第一个数据行中的原始字节（大端序，最低字节在最右）
        String[] lines = layout.split("\n");
        for (String line : lines) {
            if (line.contains("(object header: mark)")) {
                // 提取十六进制值
                int start = line.indexOf("0x");
                if (start >= 0) {
                    int end = line.indexOf(' ', start + 2);
                    if (end < 0) end = line.indexOf(')', start + 2);
                    String hex = line.substring(start + 2, end).replace("L", "");
                    long markWord = Long.parseUnsignedLong(hex, 16);
                    int tag = (int) (markWord & 0x3L);
                    return switch (tag) {
                        case 0b01 -> "无锁 (unlocked)";
                        case 0b00 -> "轻量锁 (thin lock)";
                        case 0b10 -> "重量锁 (fat lock / inflated)";
                        case 0b11 -> "GC 标记 (marked for GC)";
                        default   -> "未知";
                    };
                }
            }
        }
        return "解析失败";
    }

    // ------------------------------------------------------------------ //
    //  测试 1：VM 基本信息                                                  //
    // ------------------------------------------------------------------ //

    @Test
    void test1_vmInfo() {
        printHeader("JVM / 对象内存布局基本信息");
        System.out.println(VM.current().details());
    }

    // ------------------------------------------------------------------ //
    //  测试 2：新对象（无锁）→ 调用 hashCode → 观察 hashCode 写入 Mark Word  //
    // ------------------------------------------------------------------ //

    @Test
    void test2_unlocked_and_hashcode() {
        Object obj = new Object();

        printHeader("① 新建对象，尚未调用 hashCode()");
        printMarkWord(obj);
        System.out.printf("  >> 锁状态: %s%n", lockState(obj));

        // 调用 hashCode() 会将 identity hashcode 写入 Mark Word
        int hash = System.identityHashCode(obj);

        printHeader("② 调用 System.identityHashCode() 后  hashCode=0x" + Integer.toHexString(hash));
        printMarkWord(obj);
        System.out.printf("  >> 锁状态: %s%n", lockState(obj));
        System.out.println("  >> hashCode 已被持久化到 Mark Word 中（无锁状态的高位）");
    }

    // ------------------------------------------------------------------ //
    //  测试 3：单线程加锁 → 轻量锁                                          //
    // ------------------------------------------------------------------ //

    @Test
    void test3_thin_lock() throws InterruptedException {
        Object obj = new Object();

        printHeader("① 加锁前（无锁）");
        printMarkWord(obj);
        System.out.printf("  >> 锁状态: %s%n", lockState(obj));

        // 用一个线程加锁，在锁内暂停，让主线程观察
        CountDownLatch insideLock  = new CountDownLatch(1);
        CountDownLatch releaseLock = new CountDownLatch(1);

        Thread t = Thread.ofVirtual().start(() -> {
            synchronized (obj) {
                insideLock.countDown();         // 通知：已进入 synchronized
                try { releaseLock.await(); } catch (InterruptedException ignored) {}
            }
        });

        insideLock.await();   // 等待线程进入锁

        printHeader("② 单线程持有锁中（轻量锁）");
        printMarkWord(obj);
        System.out.printf("  >> 锁状态: %s%n", lockState(obj));
        System.out.println("  >> Mark Word 低2位 = 00，高62位指向线程栈中的 Lock Record");

        releaseLock.countDown();
        t.join();

        printHeader("③ 解锁后（轻量锁释放 → 无锁）");
        printMarkWord(obj);
        System.out.printf("  >> 锁状态: %s%n", lockState(obj));
        System.out.println("  >> 轻量锁释放后通过 CAS 将 Lock Record 置换回 Mark Word，恢复无锁");
    }

    // ------------------------------------------------------------------ //
    //  测试 4：多线程竞争 → 锁膨胀为重量锁                                  //
    // ------------------------------------------------------------------ //

    @Test
    void test4_fat_lock_inflation() throws InterruptedException {
        Object obj = new Object();

        printHeader("① 锁竞争前（无锁）");
        printMarkWord(obj);
        System.out.printf("  >> 锁状态: %s%n", lockState(obj));

        CountDownLatch t1Locked   = new CountDownLatch(1);
        CountDownLatch letT2Try   = new CountDownLatch(1);
        CountDownLatch t1Release  = new CountDownLatch(1);
        CountDownLatch allDone    = new CountDownLatch(2);

        // T1：先获得锁，等待 T2 发起竞争后才释放
        Thread t1 = Thread.ofPlatform().start(() -> {
            synchronized (obj) {
                t1Locked.countDown();
                try { t1Release.await(); } catch (InterruptedException ignored) {}
            }
            allDone.countDown();
        });

        t1Locked.await();   // T1 已拿到锁

        printHeader("② T1 持有轻量锁（此时无竞争）");
        printMarkWord(obj);
        System.out.printf("  >> 锁状态: %s%n", lockState(obj));

        // T2：与 T1 竞争，触发锁膨胀
        Thread t2 = Thread.ofPlatform().start(() -> {
            letT2Try.countDown();
            synchronized (obj) {          // 竞争失败 → 锁膨胀
                // 拿到锁后立即退出
            }
            allDone.countDown();
        });

        letT2Try.await();
        // 给 T2 足够时间发现竞争并阻塞（触发膨胀）
        LockSupport.parkNanos(20_000_000L);  // 20ms

        printHeader("③ T2 与 T1 发生竞争，锁膨胀为重量锁");
        printMarkWord(obj);
        System.out.printf("  >> 锁状态: %s%n", lockState(obj));
        System.out.println("  >> Mark Word 低2位 = 10，高62位指向堆中的 ObjectMonitor");

        t1Release.countDown();
        allDone.await();

        printHeader("④ 所有线程释放后（重量锁保持膨胀状态）");
        printMarkWord(obj);
        System.out.printf("  >> 锁状态: %s%n", lockState(obj));
        System.out.println("  >> 膨胀后的锁不会自动收缩（monitor 对象由 GC 回收）");
    }

    // ------------------------------------------------------------------ //
    //  测试 5：hashCode 与锁竞争的互斥性                                    //
    //  先调用 hashCode 再加锁 → 无法使用偏向锁（JDK18+ 已移除，此处演示         //
    //  hashCode 写入 Mark Word 后锁竞争的完整路径）                          //
    // ------------------------------------------------------------------ //

    @Test
    void test5_hashcode_then_lock() throws InterruptedException {
        Object obj = new Object();

        // 先固化 hashCode 到 Mark Word
        int hash = System.identityHashCode(obj);

        printHeader("① hashCode=" + Integer.toHexString(hash) + " 已写入 Mark Word（无锁）");
        printMarkWord(obj);
        System.out.printf("  >> 锁状态: %s%n", lockState(obj));

        CountDownLatch locked   = new CountDownLatch(1);
        CountDownLatch release  = new CountDownLatch(1);

        Thread t = Thread.ofPlatform().start(() -> {
            synchronized (obj) {
                locked.countDown();
                try { release.await(); } catch (InterruptedException ignored) {}
            }
        });

        locked.await();

        printHeader("② 有 hashCode 记录时加锁（直接轻量锁，Mark Word hashCode 被置换到 Lock Record）");
        printMarkWord(obj);
        System.out.printf("  >> 锁状态: %s%n", lockState(obj));
        System.out.println("  >> hashCode 暂存到线程栈的 Displaced Mark Word，解锁后 CAS 还原");

        release.countDown();
        t.join();

        printHeader("③ 解锁后 hashCode 从 Displaced Mark Word 还原");
        printMarkWord(obj);
        System.out.printf("  >> 锁状态: %s%n", lockState(obj));
        System.out.printf("  >> System.identityHashCode = 0x%x（与最初一致）%n",
                System.identityHashCode(obj));
    }
}
