package com.kuma.cloud.lab.transaction.blackbox.impl;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.cloud.lab.transaction.blackbox.TransactionBlackBox;
import com.kuma.cloud.lab.transaction.domain.dto.TransactionTransferDTO;
import com.kuma.cloud.lab.transaction.domain.vo.TransactionExecutedSqlVO;
import com.kuma.cloud.lab.transaction.domain.vo.TransactionRowChangeVO;
import com.kuma.cloud.lab.transaction.domain.vo.TransactionTransferVO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 基于 JDBC 的转账黑盒实现。
 */
@Component
@RequiredArgsConstructor
public class JdbcTransactionBlackBox implements TransactionBlackBox {

    private static final String ACCOUNT_TABLE = "tx_demo_account";

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransactionTransferVO execute(TransactionTransferDTO dto) {
        if (dto.getFromAccountId().equals(dto.getToAccountId())) {
            throw new BusinessException("转出账户和转入账户不能相同");
        }

        List<TransactionExecutedSqlVO> executedSql = new ArrayList<>();
        Map<Long, Map<String, Object>> beforeRows = loadAccounts(
                executedSql,
                dto.getFromAccountId(),
                dto.getToAccountId()
        );

        String debitSql = """
                UPDATE tx_demo_account
                SET balance = balance - ?
                WHERE id = ? AND balance >= ?
                """.strip();
        List<Object> debitParameters = List.of(
                dto.getAmount(),
                dto.getFromAccountId(),
                dto.getAmount()
        );
        int debitRows = jdbcTemplate.update(debitSql, debitParameters.toArray());
        executedSql.add(new TransactionExecutedSqlVO(debitSql, debitParameters, debitRows));

        if (debitRows != 1) {
            throw new BusinessException("转出账户不存在或余额不足");
        }

        if (dto.isFailAfterDebit()) {
            throw new BusinessException("按测试参数主动抛出异常，事务应回滚");
        }

        String creditSql = """
                UPDATE tx_demo_account
                SET balance = balance + ?
                WHERE id = ?
                """.strip();
        List<Object> creditParameters = List.of(
                dto.getAmount(),
                dto.getToAccountId()
        );
        int creditRows = jdbcTemplate.update(creditSql, creditParameters.toArray());
        executedSql.add(new TransactionExecutedSqlVO(creditSql, creditParameters, creditRows));

        if (creditRows != 1) {
            throw new BusinessException("转入账户不存在，事务应回滚");
        }

        Map<Long, Map<String, Object>> afterRows = loadAccounts(
                executedSql,
                dto.getFromAccountId(),
                dto.getToAccountId()
        );

        List<TransactionRowChangeVO> changes = beforeRows.entrySet().stream()
                .filter(entry -> !entry.getValue().equals(afterRows.get(entry.getKey())))
                .map(entry -> new TransactionRowChangeVO(
                        ACCOUNT_TABLE,
                        entry.getKey(),
                        entry.getValue(),
                        afterRows.get(entry.getKey())
                ))
                .toList();

        boolean rolledBack = dto.isRollbackAfterExecution();
        if (rolledBack) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return new TransactionTransferVO(rolledBack, changes, executedSql);
    }

    private Map<Long, Map<String, Object>> loadAccounts(
            List<TransactionExecutedSqlVO> executedSql,
            Long... accountIds
    ) {
        String placeholders = String.join(
                ", ",
                Collections.nCopies(accountIds.length, "?")
        );

        String sql = """
                SELECT id, owner_name, balance, update_time
                FROM tx_demo_account
                WHERE id IN (%s)
                ORDER BY id
                """.formatted(placeholders).strip();
        List<Object> parameters = new ArrayList<>(Arrays.asList(accountIds));
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, parameters.toArray());
        executedSql.add(new TransactionExecutedSqlVO(sql, parameters, rows.size()));

        return rows.stream().collect(Collectors.toMap(
                row -> ((Number) row.get("id")).longValue(),
                row -> new LinkedHashMap<>(row),
                (left, right) -> left,
                LinkedHashMap::new
        ));
    }
}
