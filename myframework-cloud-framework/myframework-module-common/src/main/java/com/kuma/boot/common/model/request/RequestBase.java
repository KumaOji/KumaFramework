/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.swagger.v3.oas.annotations.media.Schema
 *  jakarta.validation.constraints.NotBlank
 */
package com.kuma.boot.common.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@Schema(description="\u8fd4\u56de\u7ed3\u679c\u5bf9\u8c61")
public class RequestBase
implements Serializable {
    @Schema(description="orderNo")
    @NotBlank(message="orderNo\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="orderNo\u4e0d\u80fd\u4e3a\u7a7a") String orderNo;
    @Schema(description="bizNo")
    @NotBlank(message="bizNo\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="bizNo\u4e0d\u80fd\u4e3a\u7a7a") String bizNo;
    private static final long serialVersionUID = -3685249101751401211L;

    public String getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getBizNo() {
        return this.bizNo;
    }

    public void setBizNo(String bizNo) {
        this.bizNo = bizNo;
    }
}

