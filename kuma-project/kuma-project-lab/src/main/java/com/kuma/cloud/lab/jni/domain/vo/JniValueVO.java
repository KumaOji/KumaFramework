package com.kuma.cloud.lab.jni.domain.vo;

/**
 * JNI 单次调用结果。
 */
public record JniValueVO(
        String operation,
        Object input,
        Object output
) {
}
