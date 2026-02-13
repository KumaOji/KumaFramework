/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.swagger.v3.oas.annotations.media.Schema
 */
package com.kuma.boot.common.model.result;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;

@Schema(description="\u5206\u9875\u7ed3\u679c\u5bf9\u8c61")
public record PageResultRecord<R>(@Schema(description="\u603b\u6761\u6570") long totalSize, @Schema(description="\u603b\u9875\u6570") int totalPage, @Schema(description="\u5f53\u524d\u7b2c\u51e0\u9875") int currentPage, @Schema(description="\u6bcf\u9875\u663e\u793a\u6761\u6570") int pageSize, @Schema(description="\u8fd4\u56de\u6570\u636e") List<R> data) implements Serializable
{
    private static final long serialVersionUID = -275582248840137389L;

    public static <R> PageResultRecord<R> of(long totalSize, int totalPage, int currentPage, int pageSize, List<R> data) {
        return new PageResultRecord<R>(totalSize, totalPage, currentPage, pageSize, data);
    }
}

