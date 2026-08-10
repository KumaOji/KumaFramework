package com.kuma.cloud.lab.jni.domain.vo;

/**
 * JNI 场景测试中的单步操作记录。
 */
public record JniOperationStepVO(
        String operation,
        Object input,
        Object output
) {
}
