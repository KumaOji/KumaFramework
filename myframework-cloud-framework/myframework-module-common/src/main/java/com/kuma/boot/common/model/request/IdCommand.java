/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.swagger.v3.oas.annotations.media.Schema
 *  io.swagger.v3.oas.annotations.media.Schema$RequiredMode
 *  jakarta.validation.constraints.NotNull
 */
package com.kuma.boot.common.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class IdCommand
implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description="id", requiredMode=Schema.RequiredMode.REQUIRED)
    @NotNull(message="id\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotNull(message="id\u4e0d\u80fd\u4e3a\u7a7a") Long id;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

