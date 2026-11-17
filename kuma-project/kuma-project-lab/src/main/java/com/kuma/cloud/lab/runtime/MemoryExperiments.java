package com.kuma.cloud.lab.runtime;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.kuma.cloud.lab.runtime.RuntimeLearningDemo.*;

final class MemoryExperiments {
    static void regions() {
        System.out.println("--- 内存：堆、非堆、直接缓冲区与 GC 指标 ---");
        var memory = ManagementFactory.getMemoryMXBean();
        System.out.println("heap-before=" + memory.getHeapMemoryUsage());
        byte[] heap = new byte[4 * 1024 * 1024];
        for (int i = 0; i < heap.length; i += 4096) heap[i] = 7;
        ByteBuffer direct = ByteBuffer.allocateDirect(2 * 1024 * 1024);
        direct.putLong(0, 42L);
        check("堆数组与直接缓冲区读写", heap[0] == 7 && direct.isDirect() && direct.getLong(0) == 42L, "heap=4MiB, direct=2MiB");
        System.out.println("heap-after=" + memory.getHeapMemoryUsage() + "; non-heap=" + memory.getNonHeapMemoryUsage());
        ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class).forEach(pool ->
                System.out.println("buffer=" + pool.getName() + ", count=" + pool.getCount() + ", bytes=" + pool.getMemoryUsed()));
        ManagementFactory.getGarbageCollectorMXBeans().forEach(gc ->
                System.out.println("gc=" + gc.getName() + ", count=" + gc.getCollectionCount() + ", ms=" + gc.getCollectionTime()));
        Reference.reachabilityFence(heap); Reference.reachabilityFence(direct);
        System.out.println("只断言读写正确，内存使用量/GC次数是观测值；non-heap不等于全部原生内存，直接缓冲区释放时间不保证。");
    }

    static void retention() {
        System.out.println("--- 内存：强引用持有、清除容器与弱引用 ---");
        List<byte[]> cache = new ArrayList<>();
        for (int i = 0; i < 8; i++) cache.add(new byte[256 * 1024]);
        WeakReference<byte[]> weak = new WeakReference<>(cache.getFirst());
        check("容器强引用使对象保持可达", weak.get() == cache.getFirst() && cache.size() == 8, "8 x 256KiB retained");
        cache.clear();
        check("清除容器释放逻辑持有", cache.isEmpty(), cache.size());
        // A GC request does not guarantee collection; do not fail when the weak referent survives.
        System.gc();
        System.out.println("weak-reference-collected=" + (weak.get() == null) + " (observation only)");
        System.out.println("逻辑引用移除不等于RSS立即下降；示例总量2MiB，不制造OOM，也不强制断言GC时间。");
    }

    static void lifetime() throws Exception {
        System.out.println("--- 内存：Arena 显式生命周期、越界与线程约束 ---");
        MemorySegment segment;
        try (Arena arena = Arena.ofConfined()) {
            segment = arena.allocate(16, 8);
            segment.set(ValueLayout.JAVA_LONG, 0, 42L);
            check("原生段读写", segment.get(ValueLayout.JAVA_LONG, 0) == 42L, segment.byteSize());
            try { segment.get(ValueLayout.JAVA_LONG, 16); throw new IllegalStateException("Expected bounds rejection"); }
            catch (IndexOutOfBoundsException expected) { System.out.println("越界 -> IndexOutOfBoundsException (expected)"); }
            MemorySegment confined = segment;
            workers(1, pool -> {
                var result = pool.submit(() -> {
                    try { confined.get(ValueLayout.JAVA_LONG, 0); return false; }
                    catch (WrongThreadException expected) { return true; }
                });
                check("confined 段拒绝其他线程访问", get(result), true);
            });
        }
        try { segment.get(ValueLayout.JAVA_LONG, 0); throw new AssertionError("Unexpected live segment"); }
        catch (IllegalStateException expected) {
            check("close 后 scope 失效", !segment.scope().isAlive(), "IllegalStateException");
        }
    }
}
