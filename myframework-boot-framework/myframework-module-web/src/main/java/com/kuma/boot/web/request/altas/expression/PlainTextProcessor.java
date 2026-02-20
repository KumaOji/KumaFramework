package com.kuma.boot.web.request.altas.expression;

import com.kuma.boot.web.request.altas.context.LogContext;

/**
 * 纯文本表达式处理器
 * 处理不包含任何SpEL表达式的纯文本字符串
 *
 * @author nemoob
 * @since 0.2.0
 */
public class PlainTextProcessor implements com.kuma.boot.web.request.altas.expression.ExpressionProcessor {

    @Override
    public String processExpression(String expression, LogContext logContext) {
        // 纯文本直接返回原文
        return expression != null ? expression : "";
    }

    @Override
    public boolean canHandle(String expression) {
        return com.kuma.boot.web.request.altas.expression.ExpressionTypeDetector.detectType(expression) == com.kuma.boot.web.request.altas.expression.ExpressionType.PLAIN_TEXT;
    }

    @Override
    public com.kuma.boot.web.request.altas.expression.ExpressionType getSupportedType() {
        return com.kuma.boot.web.request.altas.expression.ExpressionType.PLAIN_TEXT;
    }
}
