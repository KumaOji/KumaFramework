package com.kuma.cloud.reference.domain.model;

import com.kuma.cloud.reference.exception.OrderConflictException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** 聚合根：业务规则在领域层，保持对 Spring、HTTP、数据库的零依赖。 */
public record Order(UUID id, ProductSnapshot product, int quantity, Status status, long version, Instant createdAt) {
    public enum Status { CREATED, CONFIRMED }

    public Order {
        Objects.requireNonNull(id);
        Objects.requireNonNull(product);
        Objects.requireNonNull(status);
        Objects.requireNonNull(createdAt);
        if (quantity < 1 || quantity > 1000) throw new IllegalArgumentException("数量必须在 1 到 1000 之间");
        if (version < 0) throw new IllegalArgumentException("版本号不能为负");
        product.unitPrice().multiply(quantity);
    }

    public static Order create(ProductSnapshot product, int quantity, Instant now) {
        return new Order(UUID.randomUUID(), product, quantity, Status.CREATED, 0, now);
    }

    public Money total() {
        return product.unitPrice().multiply(quantity);
    }

    public Order confirm() {
        if (status != Status.CREATED) throw new OrderConflictException("只有待确认订单可以确认");
        return new Order(id, product, quantity, Status.CONFIRMED, version + 1, createdAt);
    }
}
