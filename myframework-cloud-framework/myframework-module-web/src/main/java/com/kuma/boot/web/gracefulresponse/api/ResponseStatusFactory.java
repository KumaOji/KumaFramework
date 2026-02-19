/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.gracefulresponse.api;

import com.kuma.boot.web.gracefulresponse.data.ResponseStatus;

public interface ResponseStatusFactory {
    public ResponseStatus defaultSuccess();

    public ResponseStatus defaultError();

    public ResponseStatus newInstance(String var1, String var2);
}

