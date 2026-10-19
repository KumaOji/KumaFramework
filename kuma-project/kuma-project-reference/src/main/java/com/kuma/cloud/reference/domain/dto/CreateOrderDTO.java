package com.kuma.cloud.reference.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** 写请求与查询 QO 分开。 */
public record CreateOrderDTO(@NotBlank @Pattern(regexp = "[A-Z0-9-]{1,64}") String sku,
                                  @Min(1) @Max(1000) int quantity) {}
