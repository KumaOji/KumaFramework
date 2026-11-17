package com.kuma.cloud.lab.basics;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

import static com.kuma.cloud.lab.basics.JavaBasicsLearningDemo.check;

final class MarkWordLesson {
    private static final class Payload { int count = 42; long timestamp = 7; Object reference = new Object(); }

    static void run() {
        System.out.println("--- Mark Word：同一对象 fresh -> hash -> synchronized -> released ---");
        System.out.println("java=" + System.getProperty("java.version") + ", vm=" + System.getProperty("java.vm.name"));
        System.out.println(VM.current().details());
        Object target = new Object();
        // Do not log target/toString or compute identityHashCode before the fresh snapshot.
        System.out.println("fresh\n" + ClassLayout.parseInstance(target).toPrintable());
        int identity = System.identityHashCode(target);
        System.out.println("identityHashCode=" + identity + "\nafter hash\n" + ClassLayout.parseInstance(target).toPrintable());
        synchronized (target) {
            System.out.println("held\n" + ClassLayout.parseInstance(target).toPrintable());
            check("当前线程持锁", Thread.holdsLock(target), true);
        }
        System.out.println("released\n" + ClassLayout.parseInstance(target).toPrintable());
        check("解锁后 identity hash 稳定", System.identityHashCode(target) == identity && !Thread.holdsLock(target), identity);
        ClassLayout payload = ClassLayout.parseInstance(new Payload());
        System.out.println("字段/引用/对齐\n" + payload.toPrintable());
        check("对象大小为实际观测值", payload.instanceSize() > 0, payload.instanceSize());
        System.out.println("不固定头部字节数/锁位/锁升级路径；布局受压缩对象头、压缩指针、对齐及 JVM 实现影响。"
                + "JDK25 不演示偏向锁；JOL 0.17 对新 JVM 的解析能力有限，以运行时输出及警告为准。");
    }
}
