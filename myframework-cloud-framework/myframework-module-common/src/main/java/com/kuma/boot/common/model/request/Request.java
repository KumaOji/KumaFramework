/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.swagger.v3.oas.annotations.media.Schema
 *  jakarta.validation.constraints.NotNull
 */
package com.kuma.boot.common.model.request;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.model.request.EmptyRequest;
import com.kuma.boot.common.model.request.RequestBase;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Schema(description="\u8fd4\u56de\u7ed3\u679c\u5bf9\u8c61")
public class Request<T extends RequestBase>
implements Serializable {
    @Schema(description="\u8bf7\u6c42\u6d88\u606f\u4f53")
    private final String version = CommonConstants.DEFAULT_KMC_VERSION;
    @Schema(description="\u5f02\u5e38\u6d88\u606f\u4f53")
    private final String requestNo = IdGeneratorUtils.getIdStr();
    @Schema(description="\u8fd4\u56de\u6570\u636e")
    @NotNull(message="\u8bf7\u6c42\u53c2\u6570\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotNull(message="\u8bf7\u6c42\u53c2\u6570\u4e0d\u80fd\u4e3a\u7a7a") T order;
    private static final long serialVersionUID = -3685249101751401211L;

    public static <T extends RequestBase> Request<T> from(T order) {
        Request<T> request = new Request<T>();
        request.setOrder(order);
        return request;
    }

    public static <T extends RequestBase> Request<T> empty() {
        Request<EmptyRequest> request = new Request<EmptyRequest>();
        EmptyRequest emptyRequest = new EmptyRequest();
        request.setOrder(emptyRequest);
        return request;
    }

    public String getVersion() {
        return this.version;
    }

    public String getRequestNo() {
        return this.requestNo;
    }

    public T getOrder() {
        return this.order;
    }

    public void setOrder(T order) {
        this.order = order;
    }
}

