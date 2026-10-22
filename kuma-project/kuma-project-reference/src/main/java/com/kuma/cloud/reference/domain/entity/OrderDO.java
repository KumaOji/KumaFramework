package com.kuma.cloud.reference.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.kuma.boot.data.mybatis.mybatisplus.base.entity.MpSuperEntity;
import com.kuma.boot.common.utils.number.Money;
import com.kuma.cloud.reference.domain.model.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/** Uses the framework's Long ID and audit columns; domain rules remain in Order. */
@TableName(value = "purchase_orders", autoResultMap = true)
public class OrderDO extends MpSuperEntity<Long> {
    private String sku;
    private String productName;
    private BigDecimal unitPrice;
    private int quantity;
    private String orderStatus;
    @com.baomidou.mybatisplus.annotation.Version
    private long version;

    public String getSku() { return sku; }
    public void setSku(String value) { sku = value; }
    public String getProductName() { return productName; }
    public void setProductName(String value) { productName = value; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal value) { unitPrice = value; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int value) { quantity = value; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String value) { orderStatus = value; }
    public long getVersion() { return version; }
    public void setVersion(long value) { version = value; }
    public static OrderDO from(Order order) {
        var row = new OrderDO();
        row.setId(order.id());
        row.setSku(order.product().sku());
        row.setProductName(order.product().name());
        row.setUnitPrice(order.product().unitPrice().getAmount());
        row.setQuantity(order.quantity());
        row.setOrderStatus(order.status().name());
        row.setVersion(order.version());
        row.setCreateDate(LocalDateTime.ofInstant(order.createdAt(), ZoneOffset.UTC));
        row.setModifyDate(row.getCreateDate());
        return row;
    }

    public Order toDomain() {
        return new Order(getId(), new ProductSnapshot(sku, productName, Money.of(unitPrice, "CNY")),
                quantity, Order.Status.valueOf(orderStatus), version, getCreateDate().toInstant(ZoneOffset.UTC));
    }
}
