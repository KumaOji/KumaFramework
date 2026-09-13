package com.kuma.cloud.lab.mysql.domain.vo;

import java.util.List;
import java.util.Map;

/**
 * 一条演示 SQL 的执行结果。
 */
public record MysqlQueryResultVO(
        String topic,
        String sql,
        List<Map<String, Object>> rows,
        int affectedRows,
        String note
) {
}
