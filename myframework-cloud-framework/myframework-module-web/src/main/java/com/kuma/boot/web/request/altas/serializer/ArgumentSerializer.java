/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.request.altas.serializer;

import com.kuma.boot.web.request.altas.annotation.Log;

public interface ArgumentSerializer {
    public String serializeArgs(Object[] var1, Log var2);

    public String serializeResult(Object var1, Log var2);

    public String serialize(Object var1, int var2);
}

