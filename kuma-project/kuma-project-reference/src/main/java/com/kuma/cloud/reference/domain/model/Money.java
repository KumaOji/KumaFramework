package com.kuma.cloud.reference.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** 单币种示例，金额使用十进制；不接受隐式舍入。 */
public record Money(BigDecimal amount) {
    public Money {
        Objects.requireNonNull(amount, "金额不能为空");
        amount = amount.setScale(2, RoundingMode.UNNECESSARY);
        if (amount.signum() < 0 || amount.precision() > 19) {
            throw new IllegalArgumentException("金额必须非负且能存入 DECIMAL(19,2)");
        }
    }

    public Money multiply(int quantity) {
        if (quantity < 1 || quantity > 1000) throw new IllegalArgumentException("数量必须在 1 到 1000 之间");
        return new Money(amount.multiply(BigDecimal.valueOf(quantity)));
    }
}
