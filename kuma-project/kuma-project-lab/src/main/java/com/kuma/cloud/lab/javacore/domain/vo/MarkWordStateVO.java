package com.kuma.cloud.lab.javacore.domain.vo;

/**
 * Mark Word / 对象头观察结果。
 */
public record MarkWordStateVO(
        String stage,
        String className,
        int identityHashCode,
        String layout,
        String note
) {
}
