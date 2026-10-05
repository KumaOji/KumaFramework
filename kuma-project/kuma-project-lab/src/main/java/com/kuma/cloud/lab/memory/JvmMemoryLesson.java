package com.kuma.cloud.lab.memory;

import java.lang.management.ManagementFactory;
import java.lang.ref.Reference;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Heap objects, thread-local call frames and native direct buffers are distinct concepts. */
public final class JvmMemoryLesson {
    private JvmMemoryLesson() { }

    public static void run() throws Exception {
        var memory = ManagementFactory.getMemoryMXBean();
        System.out.println("Heap before: " + memory.getHeapMemoryUsage());
        byte[] heap = new byte[8 * 1024 * 1024];
        for (int i = 0; i < heap.length; i += 4096) heap[i] = 1;
        System.out.println("Heap after allocating 8 MiB: " + memory.getHeapMemoryUsage());
        System.out.println("Non-heap: " + memory.getNonHeapMemoryUsage());
        ManagementFactory.getMemoryPoolMXBeans().forEach(pool ->
                System.out.println("Pool " + pool.getName() + ": " + pool.getUsage()));
        ByteBuffer direct = ByteBuffer.allocateDirect(4 * 1024 * 1024);
        direct.putInt(0, 42);
        MemoryLearningDemo.check(direct.isDirect() && direct.getInt(0) == 42, "堆外缓冲区读写失败");
        ManagementFactory.getPlatformMXBeans(java.lang.management.BufferPoolMXBean.class)
                .forEach(pool -> System.out.printf("Buffer pool %s: count=%d bytes=%d%n",
                        pool.getName(), pool.getCount(), pool.getMemoryUsed()));

        // Same heap array; each worker has its own parameters, local values and call frames.
        var workers = Executors.newFixedThreadPool(2);
        try {
            var first = workers.submit(() -> stackFrame(4, 10, heap));
            var second = workers.submit(() -> stackFrame(4, 20, heap));
            MemoryLearningDemo.check(first.get(10, TimeUnit.SECONDS) == 14, "线程一局部值错误");
            MemoryLearningDemo.check(second.get(10, TimeUnit.SECONDS) == 24, "线程二局部值错误");
        } finally {
            workers.shutdownNow();
            MemoryLearningDemo.check(workers.awaitTermination(10, TimeUnit.SECONDS), "栈实验退出超时");
        }
        Reference.reachabilityFence(heap);
        Reference.reachabilityFence(direct);
        System.out.println("线程栈局部值隔离、共享堆数组、堆外缓冲区检查通过。");
        System.out.println("局部引用指向的对象仍可在堆上；JIT 可优化分配。non-heap 不是全部原生内存。GC 和堆外回收时间不作断言。");
    }

    private static int stackFrame(int depth, int local, byte[] shared) {
        if (depth == 0) {
            MemoryLearningDemo.check(shared[0] == 1, "共享堆对象不可见");
            return local;
        }
        return stackFrame(depth - 1, local + 1, shared);
    }
}
