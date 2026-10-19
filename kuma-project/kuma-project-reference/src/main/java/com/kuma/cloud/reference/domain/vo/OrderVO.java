package com.kuma.cloud.reference.domain.vo;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderVO(UUID id, String sku, String productName, BigDecimal unitPrice,
                      int quantity, BigDecimal totalAmount, String status, long version, Instant createdAt) {}
