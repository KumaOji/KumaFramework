package com.kuma.cloud.reference.domain.qo;

import com.kuma.cloud.reference.domain.enums.DataScope;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

/** HTTP 查询条件与分页边界。 */
public record OrderPageQO(DataScope scope, @Pattern(regexp = "[A-Z0-9-]{1,64}") String sku,
        @Pattern(regexp = "CREATED|CONFIRMED") String status,
        @Min(1) @Max(100000) Integer page, @Min(1) @Max(100) Integer size) {
    public OrderPageQO {
        scope = scope == null ? DataScope.MAIN : scope;
        page = page == null ? 1 : page;
        size = size == null ? 20 : size;
    }
}
