package com.kuma.cloud.lab.transaction.domain.vo;

import java.util.List;

/**
 * 数据库事务转账测试结果。
 */
public record TransactionTransferVO(
        boolean rolledBack,
        List<TransactionRowChangeVO> changes,
        List<TransactionExecutedSqlVO> executedSql
) {
}
