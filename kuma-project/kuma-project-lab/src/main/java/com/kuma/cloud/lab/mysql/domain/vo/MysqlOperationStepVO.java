package com.kuma.cloud.lab.mysql.domain.vo;

/**
 * MySQL 场景测试单步记录。
 */
public record MysqlOperationStepVO(
        String topic,
        String operation,
        Object detail,
        String note
) {
}
