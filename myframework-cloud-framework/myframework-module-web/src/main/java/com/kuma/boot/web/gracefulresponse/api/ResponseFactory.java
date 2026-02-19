/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.gracefulresponse.api;

import com.kuma.boot.web.gracefulresponse.data.Response;
import com.kuma.boot.web.gracefulresponse.data.ResponseStatus;

public interface ResponseFactory {
    public Response newEmptyInstance();

    public Response newInstance(ResponseStatus var1);

    public Response newSuccessInstance();

    public Response newSuccessInstance(Object var1);

    public Response newFailInstance();
}

