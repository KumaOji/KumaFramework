/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.swagger.v3.oas.annotations.media.Schema
 *  io.swagger.v3.oas.annotations.media.Schema$RequiredMode
 *  jakarta.validation.constraints.Max
 *  jakarta.validation.constraints.Min
 *  jakarta.validation.constraints.NotNull
 */
package com.kuma.boot.common.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class PageQuery
implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description="\u5f53\u524d\u7b2c\u51e0\u9875\uff0c\u9ed8\u8ba41", example="1", type="integer", requiredMode=Schema.RequiredMode.REQUIRED, defaultValue="1")
    @NotNull(message="\u5f53\u524d\u9875\u663e\u793a\u6570\u91cf\u4e0d\u80fd\u4e3a\u7a7a")
    @Min(value=0L, message="\u5f53\u524d\u9875\u6570\u4e0d\u80fd\u5c0f\u4e8e0")
    @Max(value=0x7FFFFFFFL)
    private @NotNull(message="\u5f53\u524d\u9875\u663e\u793a\u6570\u91cf\u4e0d\u80fd\u4e3a\u7a7a") @Min(value=0L, message="\u5f53\u524d\u9875\u6570\u4e0d\u80fd\u5c0f\u4e8e0") @Max(value=0x7FFFFFFFL) Integer currentPage;
    @Schema(description="\u6bcf\u9875\u663e\u793a\u6761\u6570\uff0c\u9ed8\u8ba410", example="20", type="integer", requiredMode=Schema.RequiredMode.REQUIRED, defaultValue="20")
    @NotNull(message="\u6bcf\u9875\u6570\u636e\u663e\u793a\u6570\u91cf\u4e0d\u80fd\u4e3a\u7a7a")
    @Min(value=5L, message="\u6bcf\u9875\u663e\u793a\u6761\u6570\u6700\u5c0f\u4e3a5\u6761")
    @Max(value=100L, message="\u6bcf\u9875\u663e\u793a\u6761\u6570\u6700\u5927\u4e3a100\u6761")
    private @NotNull(message="\u6bcf\u9875\u6570\u636e\u663e\u793a\u6570\u91cf\u4e0d\u80fd\u4e3a\u7a7a") @Min(value=5L, message="\u6bcf\u9875\u663e\u793a\u6761\u6570\u6700\u5c0f\u4e3a5\u6761") @Max(value=100L, message="\u6bcf\u9875\u663e\u793a\u6761\u6570\u6700\u5927\u4e3a100\u6761") Integer pageSize;
    @Schema(description="\u6392\u5e8f\u5b57\u6bb5")
    private String sort;
    @Schema(description="\u6392\u5e8f\u65b9\u5f0f asc/desc")
    private String order;

    public Integer getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSort() {
        return this.sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return this.order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public PageQuery pageQuery() {
        return this;
    }
}

