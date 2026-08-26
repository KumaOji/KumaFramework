package com.kuma.cloud.lab.javacore.domain.vo;

/**
 * Java 基础场景测试单步记录。
 */
public record JavaCoreOperationStepVO(
        String topic,
        String operation,
        Object detail,
        String note
) {
}
