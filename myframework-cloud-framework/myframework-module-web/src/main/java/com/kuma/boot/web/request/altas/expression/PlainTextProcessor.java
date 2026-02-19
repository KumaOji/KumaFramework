/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.request.altas.expression;

import com.kuma.boot.web.request.altas.context.LogContext;

public class PlainTextProcessor
implements ExpressionProcessor {
    @Override
    public String processExpression(String expression, LogContext logContext) {
        return expression != null ? expression : "";
    }

    @Override
    public boolean canHandle(String expression) {
        return ExpressionTypeDetector.detectType(expression) == ExpressionType.PLAIN_TEXT;
    }

    @Override
    public ExpressionType getSupportedType() {
        return ExpressionType.PLAIN_TEXT;
    }
}

