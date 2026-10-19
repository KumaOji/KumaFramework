package com.kuma.cloud.reference.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;

/** 写请求与查询 QO 分开。 */
public record CreateOrderDTO(@NotBlank @Pattern(regexp = "[A-Z0-9-]{1,64}") String sku,
        @NotBlank @Size(max = 120) String productName,
        @NotNull @DecimalMin("0.00") @Digits(integer = 17, fraction = 2) BigDecimal unitPrice,
        @Min(1) @Max(1000) int quantity) {}
