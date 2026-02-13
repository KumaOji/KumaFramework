/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.swagger.v3.oas.annotations.media.Schema
 *  io.swagger.v3.oas.annotations.media.Schema$RequiredMode
 *  jakarta.validation.constraints.NotEmpty
 *  jakarta.validation.constraints.Size
 */
package com.kuma.boot.common.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

public class IdsCommand
implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description="id\u5217\u8868", requiredMode=Schema.RequiredMode.REQUIRED)
    @NotEmpty(message="id\u5217\u8868\u4e0d\u80fd\u4e3a\u7a7a")
    @Size(max=100, message="id\u5217\u8868\u6700\u5927\u4e3a{max}\u6761")
    private @NotEmpty(message="id\u5217\u8868\u4e0d\u80fd\u4e3a\u7a7a") @Size(max=100, message="id\u5217\u8868\u6700\u5927\u4e3a{max}\u6761") List<Long> ids;

    public List<Long> getIds() {
        return this.ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}

