package com.kuma.cloud.lab.memory;

import com.kuma.cloud.lab.lock.LockLearningDemo;

/** JDK-only entry point; checks do not require -ea, Spring or middleware. */
public final class MemoryLearningDemo {
    private MemoryLearningDemo() { }

    public static void main(String[] args) throws Exception {
        LockLearningDemo.main(new String[0]);
        JmmLesson.run();
        JvmMemoryLesson.run();
        System.out.println("全部 Java 内存实验通过；Linux 实验见 scripts/run-linux-memory-lab.sh。");
    }

    static void check(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
