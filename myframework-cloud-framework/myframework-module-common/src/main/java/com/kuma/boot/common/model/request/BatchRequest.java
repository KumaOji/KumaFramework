/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.swagger.v3.oas.annotations.media.Schema
 */
package com.kuma.boot.common.model.request;

import com.kuma.boot.common.model.request.RequestBase;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description="\u5206\u9875\u7ed3\u679c\u5bf9\u8c61")
public class BatchRequest<T>
extends RequestBase {
    private static final long serialVersionUID = -3685249101751401211L;
    private List<T> data;

    public static <T> BatchRequest<T> from(List<T> data) {
        BatchRequest<T> batchResponse = new BatchRequest<T>();
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

