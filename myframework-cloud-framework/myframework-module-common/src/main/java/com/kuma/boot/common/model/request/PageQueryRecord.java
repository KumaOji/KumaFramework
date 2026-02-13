/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnore
 *  io.swagger.v3.oas.annotations.media.Schema
 *  io.swagger.v3.oas.annotations.media.Schema$RequiredMode
 *  jakarta.validation.constraints.Max
 *  jakarta.validation.constraints.Min
 *  jakarta.validation.constraints.NotNull
 */
package com.kuma.boot.common.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Schema(name="PageQuery", description="\u901a\u7528\u5206\u9875\u67e5\u8be2Query")
public record PageQueryRecord<QueryDTO>(@Schema(description="\u5f53\u524d\u7b2c\u51e0\u9875\uff0c\u9ed8\u8ba41", example="1", requiredMode=Schema.RequiredMode.REQUIRED) @NotNull(message="\u5f53\u524d\u9875\u663e\u793a\u6570\u91cf\u4e0d\u80fd\u4e3a\u7a7a") @Min(value=0L) @Max(value=0x7FFFFFFFL) @NotNull(message="\u5f53\u524d\u9875\u663e\u793a\u6570\u91cf\u4e0d\u80fd\u4e3a\u7a7a") @Min(value=0L) @Max(value=0x7FFFFFFFL) Integer currentPage, @Schema(description="\u6bcf\u9875\u663e\u793a\u6761\u6570\uff0c\u9ed8\u8ba410", example="10", requiredMode=Schema.RequiredMode.REQUIRED) @NotNull(message="\u6bcf\u9875\u6570\u636e\u663e\u793a\u6570\u91cf\u4e0d\u80fd\u4e3a\u7a7a") @Min(value=5L) @Max(value=100L) @NotNull(message="\u6bcf\u9875\u6570\u636e\u663e\u793a\u6570\u91cf\u4e0d\u80fd\u4e3a\u7a7a") @Min(value=5L) @Max(value=100L) Integer pageSize, @Schema(description="\u67e5\u8be2\u53c2\u6570\u5bf9\u8c61") QueryDTO query) implements Serializable
{
    private static final long serialVersionUID = -2483306509077581330L;

    @JsonIgnore
    public long offset() {
        long current = this.currentPage.intValue();
        if (current <= 1L) {
            return 0L;
        }
        return (current - 1L) * (long)this.pageSize.intValue();
    }
}

