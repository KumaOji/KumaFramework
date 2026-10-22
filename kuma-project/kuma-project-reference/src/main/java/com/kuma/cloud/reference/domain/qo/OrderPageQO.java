package com.kuma.cloud.reference.domain.qo;

import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.reference.domain.enums.DataScope;
import jakarta.validation.constraints.Pattern;

/** Framework pagination plus order-specific filters. */
public class OrderPageQO extends PageQuery {
    private DataScope scope = DataScope.MAIN;
    @Pattern(regexp = "[A-Z0-9-]{1,64}")
    private String sku;
    @Pattern(regexp = "CREATED|CONFIRMED")
    private String status;
    public DataScope getScope() { return scope; }
    public void setScope(DataScope value) { scope = value; }
    public String getSku() { return sku; }
    public void setSku(String value) { sku = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
}
