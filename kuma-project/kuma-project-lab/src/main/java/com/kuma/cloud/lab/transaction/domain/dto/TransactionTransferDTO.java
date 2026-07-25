package com.kuma.cloud.lab.transaction.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 数据库事务转账测试参数。
 */
@Data
public class TransactionTransferDTO {

    @NotNull(message = "转出账户不能为空")
    private Long fromAccountId;

    @NotNull(message = "转入账户不能为空")
    private Long toAccountId;

    @NotNull(message = "转账金额不能为空")
    @DecimalMin(value = "0.01", message = "转账金额必须大于 0")
    private BigDecimal amount;

    /**
     * true：扣款成功后主动抛异常，用于验证异常回滚。
     */
    private boolean failAfterDebit;

    /**
     * true：执行全部 SQL、收集变更结果后回滚事务。
     */
    private boolean rollbackAfterExecution;
}
