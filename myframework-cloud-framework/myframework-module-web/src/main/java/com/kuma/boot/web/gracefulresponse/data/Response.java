/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.gracefulresponse.data;

public interface Response {
    public void setStatus(ResponseStatus var1);

    public ResponseStatus getStatus();

    public void setPayload(Object var1);

    public Object getPayload();
}

