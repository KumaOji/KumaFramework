/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.gracefulresponse.defaults;

import com.kuma.boot.web.gracefulresponse.data.Response;
import com.kuma.boot.web.gracefulresponse.data.ResponseStatus;
import java.util.Collections;

public class DefaultResponseImplStyle0
implements Response {
    private ResponseStatus status;
    private Object payload = Collections.emptyMap();

    public DefaultResponseImplStyle0() {
    }

    public DefaultResponseImplStyle0(Object payload) {
        this.payload = payload;
    }

    @Override
    public void setStatus(ResponseStatus responseStatus) {
        this.status = responseStatus;
    }

    @Override
    public ResponseStatus getStatus() {
        return this.status;
    }

    @Override
    public void setPayload(Object obj) {
        this.payload = obj;
    }

    @Override
    public Object getPayload() {
        return this.payload;
    }
}

