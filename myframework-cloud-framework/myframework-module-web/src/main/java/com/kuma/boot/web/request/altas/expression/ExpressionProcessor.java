/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.request.altas.expression;

import com.kuma.boot.web.request.altas.context.LogContext;

public interface ExpressionProcessor {
    public String processExpression(String var1, LogContext var2);

    public boolean canHandle(String var1);

    public ExpressionType getSupportedType();
}

