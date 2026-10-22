package com.kuma.cloud.reference.domain.model;

import com.kuma.boot.common.utils.number.Money;
import java.util.Objects;

/** 下单时的商品快照，商品之后改价不会改变已有订单。 */
public record ProductSnapshot(String sku, String name, Money unitPrice) {
    public ProductSnapshot {
        if (sku == null || !sku.matches("[A-Z0-9-]{1,64}")) throw new IllegalArgumentException("商品编码格式不正确");
        if (name == null || name.isBlank() || name.length() > 120) throw new IllegalArgumentException("商品名称不能为空且不能超过 120 字符");
        Objects.requireNonNull(unitPrice, "单价不能为空");
        if (!unitPrice.getCurrency().getCurrencyCode().equals("CNY") || unitPrice.getAmount().signum() < 0) {
            throw new IllegalArgumentException("示例订单单价必须为非负人民币金额");
        }
    }
}
