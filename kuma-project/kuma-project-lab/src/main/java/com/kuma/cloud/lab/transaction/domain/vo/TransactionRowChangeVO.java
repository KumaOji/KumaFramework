package com.kuma.cloud.lab.transaction.domain.vo;

import java.util.Map;

/**
 * 事务测试中一行数据的修改前后快照。
 */
public record TransactionRowChangeVO(
        String tableName,
        Long rowId,
        Map<String, Object> before,
        Map<String, Object> after
) {
}
