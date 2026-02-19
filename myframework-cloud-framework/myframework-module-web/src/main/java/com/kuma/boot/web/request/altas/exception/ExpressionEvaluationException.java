/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.request.altas.exception;

public class ExpressionEvaluationException
extends LogException {
    private static final long serialVersionUID = 1L;
    private final String expression;

    public ExpressionEvaluationException(String expression, String message) {
        super("\u8868\u8fbe\u5f0f\u8bc4\u4f30\u5931\u8d25: " + expression + ", \u539f\u56e0: " + message);
        this.expression = expression;
    }

    public ExpressionEvaluationException(String expression, String message, Throwable cause) {
        super("\u8868\u8fbe\u5f0f\u8bc4\u4f30\u5931\u8d25: " + expression + ", \u539f\u56e0: " + message, cause);
        this.expression = expression;
    }

    public ExpressionEvaluationException(String expression, Throwable cause) {
        super("\u8868\u8fbe\u5f0f\u8bc4\u4f30\u5931\u8d25: " + expression, cause);
        this.expression = expression;
    }

    public String getExpression() {
        return this.expression;
    }
}

