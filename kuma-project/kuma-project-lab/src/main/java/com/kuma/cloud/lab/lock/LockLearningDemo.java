package com.kuma.cloud.lab.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 学习入口：先读 package-info.java，再按输出顺序阅读五个 Lesson 的注释。
 * 直接运行 main，无需启动 Spring、Nacos。检查不依赖 -ea，失败或超时会抛出异常。
 * 这些实验验证同步语义，不是性能基准；性能比较应另用 JMH。
 */
public final class LockLearningDemo {
    private LockLearningDemo() {
    }

    public static void main(String[] args) throws Exception {
        VolatileLesson.run();
        CasLesson.run();
        ReentrantLockLesson.run();
        AqsLesson.run();
        CountDownLatchLesson.run();
        System.out.println("全部并发学习实验通过，请结合各 Lesson 的代码注释阅读。");
    }

    /** Future.get 传播线程异常并建立可见性；超时与 finally 清理防止实验无限挂起。 */
    @SafeVarargs
    static void parallel(Callable<Void>... tasks) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(tasks.length);
        List<Future<Void>> futures = new ArrayList<>();
        try {
            for (Callable<Void> task : tasks) {
                futures.add(executor.submit(task));
            }
            for (Future<Void> future : futures) {
                future.get(10, TimeUnit.SECONDS);
            }
        } finally {
            for (Future<Void> future : futures) {
                future.cancel(true);
            }
            executor.shutdownNow();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("实验线程未按时退出");
            }
        }
    }

    static void check(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
