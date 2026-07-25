package com.kuma.cloud.lab.transaction.domain.vo;

import java.util.List;

/**
 * 事务测试中执行的一条 SQL。
 *
 * @param sql          带占位符的 SQL
 * @param parameters   按占位符顺序排列的参数
 * @param affectedRows 更新影响行数；查询语句为返回行数
 */
public record TransactionExecutedSqlVO(
        String sql,
        List<Object> parameters,
        int affectedRows
) {
}
