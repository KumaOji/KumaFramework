package com.kuma.cloud.lab.spring.domain.vo;

/**
 * Spring 场景测试单步记录。
 */
public record SpringOperationStepVO(
        String topic,
        String operation,
        Object detail,
        String note
) {
}
