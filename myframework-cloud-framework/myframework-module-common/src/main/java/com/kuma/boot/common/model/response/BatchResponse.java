/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.swagger.v3.oas.annotations.media.Schema
 */
package com.kuma.boot.common.model.response;

import com.kuma.boot.common.model.response.ResponseBase;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description="\u7ed3\u679c\u5bf9\u8c61")
public class BatchResponse<T>
extends ResponseBase {
    private static final long serialVersionUID = -3685249101751401211L;
    @Schema(description="\u7ed3\u679c")
    private List<T> data;

    public static <T> BatchResponse<T> from(List<T> data) {
        BatchResponse<T> batchResponse = new BatchResponse<T>();
        batchResponse.setData(data);
        return batchResponse;
    }

    public List<T> getData() {
        return this.data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}

