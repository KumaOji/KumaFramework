package com.kuma.cloud.reference.domain.vo;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderVO(Long id, String sku, String productName, BigDecimal unitPrice,
                      int quantity, BigDecimal totalAmount, String status, long version, Instant createdAt) {}
