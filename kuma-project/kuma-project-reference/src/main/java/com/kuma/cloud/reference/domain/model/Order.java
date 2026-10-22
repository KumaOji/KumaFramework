package com.kuma.cloud.reference.domain.model;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.utils.number.Money;

import java.time.Instant;
import java.util.Objects;

/** 聚合根：业务规则在领域层，保持对 Spring、HTTP、数据库的零依赖。 */
public record Order(Long id, ProductSnapshot product, int quantity, Status status, long version, Instant createdAt) {
    public enum Status { CREATED, CONFIRMED }

    public Order {
        if (id != null && id < 1) throw new IllegalArgumentException("订单 ID 必须为正数");
        Objects.requireNonNull(product);
        Objects.requireNonNull(status);
        Objects.requireNonNull(createdAt);
        if (quantity < 1 || quantity > 1000) throw new IllegalArgumentException("数量必须在 1 到 1000 之间");
        if (version < 0) throw new IllegalArgumentException("版本号不能为负");
        product.unitPrice().multiply(quantity);
    }

    public static Order create(ProductSnapshot product, int quantity, Instant now) {
        return new Order(null, product, quantity, Status.CREATED, 0, now);
    }

    public Money total() {
        return product.unitPrice().multiply(quantity);
    }

    public Order confirm() {
        if (status != Status.CREATED) throw new BusinessException(ResultEnum.REQUEST_OTHER_OPERATION, "只有待确认订单可以确认");
        return new Order(id, product, quantity, Status.CONFIRMED, version + 1, createdAt);
    }
}
