package com.kuma.cloud.lab.javacore.support;

import com.kuma.cloud.lab.javacore.domain.vo.MarkWordStateVO;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

/**
 * 基于 JOL（Java Object Layout）观察对象头与 Mark Word 变化。
 */
public final class MarkWordInspector {

    private MarkWordInspector() {
    }

    public static String vmDetails() {
        return VM.current().details();
    }

    public static MarkWordStateVO inspectFreshObject() {
        Object target = new Object();
        return layoutOf("新建空对象", target, identityHash(target), "偏向锁/无锁状态下的 Mark Word");
    }

    public static MarkWordStateVO inspectAfterHashCode() {
        Object target = new Object();
        int hash = target.hashCode();
        return layoutOf("调用 hashCode() 后", target, hash, "未偏向时 hashCode 会写入 Mark Word，可能影响锁升级路径");
    }

    public static MarkWordStateVO inspectBiasedLockCandidate() {
        Object target = new Object();
        synchronized (target) {
            return layoutOf("synchronized 持锁中", target, identityHash(target), "轻量级/重量级锁会在 Mark Word 中记录锁信息");
        }
    }

    public static MarkWordStateVO inspectInstanceWithFields() {
        SamplePayload payload = new SamplePayload(42, "javacore");
        return layoutOf("含字段实例对象", payload, identityHash(payload), "实例数据紧随对象头，字段对齐受 JVM 影响");
    }

    public static String klassLayout() {
        return ClassLayout.parseClass(SamplePayload.class).toPrintable();
    }

    private static MarkWordStateVO layoutOf(String stage, Object target, int identityHash, String note) {
        return new MarkWordStateVO(
                stage,
                target.getClass().getName(),
                identityHash,
                ClassLayout.parseInstance(target).toPrintable(),
                note);
    }

    private static int identityHash(Object target) {
        return System.identityHashCode(target);
    }

    private static final class SamplePayload {

        private final int id;
        private final String name;

        private SamplePayload(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

}
