package com.kuma.cloud.reference.domain.entity;

import com.kuma.cloud.reference.domain.model.Money;
import com.kuma.cloud.reference.domain.model.Order;
import com.kuma.cloud.reference.domain.model.ProductSnapshot;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** 数据库行快照，不直接暴露到 HTTP。 */
public record OrderDO(String id, String sku, String productName, BigDecimal unitPrice,
                      int quantity, String orderStatus, long version, Instant createdAt) {
    public static OrderDO from(Order order) {
        return new OrderDO(order.id().toString(), order.product().sku(), order.product().name(),
                order.product().unitPrice().amount(), order.quantity(), order.status().name(),
                order.version(), order.createdAt());
    }

    public Order toDomain() {
        return new Order(UUID.fromString(id), new ProductSnapshot(sku, productName, new Money(unitPrice)),
                quantity, Order.Status.valueOf(orderStatus), version, createdAt);
    }
}
